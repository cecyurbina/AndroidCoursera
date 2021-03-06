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
package org.magnum.mobilecloud.video.controller;


import java.io.IOException;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.magnum.mobilecloud.video.client.VideoSvcApi;
import org.magnum.mobilecloud.video.model.AverageVideoRating;
import org.magnum.mobilecloud.video.model.Video;
import org.magnum.mobilecloud.video.model.VideoRepository;
import org.magnum.mobilecloud.video.model.VideoStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;

import retrofit.http.Path;
import retrofit.http.Streaming;

import java.security.Principal;


@Controller
public class VideoSvc {
    @Autowired
    private VideoRepository videoRepository;
	//private List<Video> videos = new CopyOnWriteArrayList<Video>();
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
        return Lists.newArrayList(videoRepository.findAll());
	}

	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video v) {
		// TODO Auto-generated method stub
		//v.setContentType(contentType);
		//v.setDataUrl(VIDEO_SVC_PATH);
		//v.setDuration(duration);
		//v.setTotalVotes(0);
		//v.setTotalRating(0);
		//v.setDataUrl(getDataUrl(id));
		
		//int id = videos.size() + 1;
		//v.setId(id);
		//videos.add(v);
        videoRepository.save(v);
		return v;
	}
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}", method=RequestMethod.GET)
    public @ResponseBody Video getVideoById(@PathVariable long id, HttpServletResponse response) {
        Video video = videoRepository.findOne(id);

        if (video == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

        return video;
    }


	@RequestMapping(value = VideoSvcApi.VIDEO_DATA_PATH, method = RequestMethod.POST)
	public @ResponseBody VideoStatus setVideoData(
			@PathVariable("id") long id,
			@RequestPart(VideoSvcApi.DATA_PARAMETER) MultipartFile videoData,
			HttpServletResponse mResponse, Principal principal) {
		
		 VideoStatus videoStatus = new VideoStatus(VideoStatus.VideoState.PROCESSING);

	        try {
	            Video video = getVideoById(id);
	            if (video != null) {
	            	video.getUsersWhoRatingVideo();
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
    public void getVideoData(@PathVariable(VideoSvcApi.ID_PARAMETER) long id,
                                       HttpServletResponse response) {
        Video video = getVideoById(id);
        if (video == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            if (response.getContentType() == null) {
                response.setContentType(video.getContentType());
            }
            try {
                videoFileManager.copyVideoData(video, response.getOutputStream());
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

	
	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH+"/{id}/rating/{rating}", method = RequestMethod.POST)
	public @ResponseBody AverageVideoRating rateVideo(
			@PathVariable("id") long id, @PathVariable("rating") int rating,
			HttpServletResponse mResponse, Principal principal) {
		
		 VideoStatus videoStatus = new VideoStatus(VideoStatus.VideoState.PROCESSING);
		 AverageVideoRating average = null;
	        try {
	            Video video = getVideoById(id);
	            if (video != null) {
	                int totalR = video.getTotalVotes();
	                float totalPoints = video.getTotalRating();
	               if (video.ratingVideo(principal.getName())) {
	            	   
	            	   videoRepository.save(video);
	            	   average = new AverageVideoRating(totalPoints, id, totalR);
	               }
	               else {
		                mResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	               }
	            } else {
	                mResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return average;
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
		Video video = videoRepository.findOne(id);
        if (video == null) {
            return null;
        }
        return video;
    }
	
    public void saveVideo(Video v, MultipartFile videoData) throws IOException {
        videoFileManager.saveVideoData(v, videoData.getInputStream());
    }
    
    public void saveRatingVideo(Video v, int rating) throws IOException {
    	v.setRating(rating);
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
