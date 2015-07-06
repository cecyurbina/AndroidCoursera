/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.magnum.dataup;


import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import retrofit.client.Response;
import retrofit.http.Streaming;
import retrofit.mime.TypedFile;


@Controller
public class VideoSvc {
	private List<Video> videos = new CopyOnWriteArrayList<Video>();
	private VideoFileManager videoFileManager;
	//private Collection<Video> videoList;
    //private AtomicLong counter;
    
	@PostConstruct
    public void init() throws IOException {
        //videoList = new CopyOnWriteArrayList<Video>();
        //counter = new AtomicLong(10);
        videoFileManager = VideoFileManager.get();
    }


	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideoList() {
		return videos;
	}

	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video v) {
		// TODO Auto-generated method stub
		//v.setContentType(contentType);
		//v.setDataUrl(VIDEO_SVC_PATH);
		//v.setDuration(duration);
		int id = videos.size() + 1;
		v.setId(id);
		v.setDataUrl(getDataUrl(id));
		//v.setLocation(location);
		videos.add(v);
		return v;
	}
	

	@RequestMapping(value = VideoSvcApi.VIDEO_DATA_PATH, method = RequestMethod.POST)
	public @ResponseBody VideoStatus setVideoData(
			@PathVariable("id") long id,
			@RequestPart(VideoSvcApi.DATA_PARAMETER) MultipartFile videoData,
			HttpServletResponse mResponse) {
		
		 VideoStatus videoStatus = new VideoStatus(VideoStatus.VideoState.PROCESSING);

	        try {
	            Video video = getVideoById(id);
	            if (video != null) {
	                saveVideo(video, videoData);
	                videoStatus = new VideoStatus(VideoStatus.VideoState.READY);
	            } else {
	                mResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return videoStatus;
	}
	
	@Streaming
    @RequestMapping(value = VideoSvcApi.VIDEO_DATA_PATH, method = RequestMethod.GET)
    public HttpServletResponse getData(@PathVariable(VideoSvcApi.ID_PARAMETER) long id,
                                       HttpServletResponse response) {
        Video video = getVideoById(id);
        if (video == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            if (response.getContentType() == null) {
                response.setContentType("video/mp4");
            }
            try {
                videoFileManager.copyVideoData(video, response.getOutputStream());
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
        return response;
    }

	/**
	 * You will need to create one or more Spring controllers to fulfill the
	 * requirements of the assignment. If you use this file, please rename it
	 * to something other than "AnEmptyController"
	 * 
	 * 
		 ________  ________  ________  ________          ___       ___  ___  ________  ___  __       
		|\   ____\|\   __  \|\   __  \|\   ___ \        |\  \     |\  \|\  \|\   ____\|\  \|\  \     
		\ \  \___|\ \  \|\  \ \  \|\  \ \  \_|\ \       \ \  \    \ \  \\\  \ \  \___|\ \  \/  /|_   
		 \ \  \  __\ \  \\\  \ \  \\\  \ \  \ \\ \       \ \  \    \ \  \\\  \ \  \    \ \   ___  \  
		  \ \  \|\  \ \  \\\  \ \  \\\  \ \  \_\\ \       \ \  \____\ \  \\\  \ \  \____\ \  \\ \  \ 
		   \ \_______\ \_______\ \_______\ \_______\       \ \_______\ \_______\ \_______\ \__\\ \__\
		    \|_______|\|_______|\|_______|\|_______|        \|_______|\|_______|\|_______|\|__| \|__|
                                                                                                                                                                                                                                                                        
	 * 
	 */
	
	private Video getVideoById(Long id) {
        for (Video v : videos) {
            if (v.getId() == id) return v;
        }
        return null;
    }
	
    public void saveVideo(Video v, MultipartFile videoData) throws IOException {
        videoFileManager.saveVideoData(v, videoData.getInputStream());
    }
    
	 private String getDataUrl(long videoId){
         String url = getUrlBaseForLocalServer() + "/video/" + videoId + "/data";
         return url;
     }

	 private String getUrlBaseForLocalServer() {
         HttpServletRequest request = 
             ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
         String base = 
            "http://"+request.getServerName() 
            + ((request.getServerPort() != 80) ? ":"+request.getServerPort() : "");
         return base;
      }
	
}
