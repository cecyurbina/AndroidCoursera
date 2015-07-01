package vandy.mooc.jsonweather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Parses the Json weather data returned from the Weather Services API
 * and returns a List of JsonWeather objects that contain this data.
 */
public class WeatherJSONParser {
    /**
     * Used for logging purposes.
     */
    private final String TAG =
        this.getClass().getCanonicalName();
    private JSONObject json;

    /**
     * Parse the @a inputStream and convert it into a List of JsonWeather
     * objects.
     */
    public List<JsonWeather> parseJsonStream(InputStream inputStream)
            throws IOException, JSONException {

        // TODO -- you fill in here.
        // Create a JsonReader for the inputStream.
        try (JsonReader reader =
                     new JsonReader(new InputStreamReader(inputStream,
                             "UTF-8"))) {
            // Log.d(TAG, "Parsing the results returned as an array");

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            json = new JSONObject(responseStrBuilder.toString());
            Log.d("$$$$$$$$$$$$$$$44", json.toString());
            // Handle the array returned from the Acronym Service.
            return parseJsonWeatherArray(reader);
        }
    }

    /**
     * Parse a Json stream and convert it into a List of JsonWeather
     * objects.
     */
    public List<JsonWeather> parseJsonWeatherArray(JsonReader reader)
            throws IOException, JSONException {
        List<JsonWeather> list = new ArrayList<>();
        list.add(parseJsonWeather());
        return list;
        // TODO -- you fill in here.

    }

    /**
     * Parse a Json stream and return a JsonWeather object.
     */
    public JsonWeather parseJsonWeather(JsonReader reader) 
        throws IOException {
        reader.beginObject();
        JsonWeather weatherJson = new JsonWeather();
        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                Log.d("###########", reader.toString());
                switch (name) {
                    case JsonWeather.cod_JSON:
                        weatherJson.setCod(reader.nextLong());
                        break;
                    case JsonWeather.name_JSON:
                        weatherJson.setName(reader.nextString());
                        break;
                    case JsonWeather.id_JSON:
                        weatherJson.setId(reader.nextLong());
                        break;
                    case JsonWeather.dt_JSON:
                        weatherJson.setDt(reader.nextLong());
                        break;
                    case JsonWeather.wind_JSON:
                        weatherJson.setWind(parseWind(reader));
                        break;
                    case JsonWeather.main_JSON:
                        weatherJson.setMain(parseMain(reader));
                        break;
                    case JsonWeather.base_JSON:
                        weatherJson.setBase(reader.nextString());
                        break;
                    case JsonWeather.weather_JSON:
                        weatherJson.setWeather(parseWeathers(reader));
                        break;
                    case JsonWeather.sys_JSON:
                        weatherJson.setSys(parseSys(reader));
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
        } finally {
            reader.endObject();
        }
        return weatherJson;

    }


    /**
     * Parse a Json stream and return a JsonWeather object.
     */
    public JsonWeather parseJsonWeather()
            throws IOException, JSONException {
        JsonWeather weatherJson = new JsonWeather();
        weatherJson.setCod(json.getLong(JsonWeather.cod_JSON));
        weatherJson.setName(json.getString(JsonWeather.name_JSON));
        weatherJson.setId(json.getLong(JsonWeather.id_JSON));
        weatherJson.setDt(json.getLong(JsonWeather.dt_JSON));
        weatherJson.setBase(json.getString(JsonWeather.base_JSON));
        JSONObject windJson = json.getJSONObject(JsonWeather.wind_JSON);

        //wind
        Wind wind = new Wind();
        wind.setSpeed(windJson.getDouble(Wind.speed_JSON));
        wind.setDeg(windJson.getDouble(Wind.deg_JSON));
        weatherJson.setWind(wind);

        //main
        JSONObject mainJson = json.getJSONObject(JsonWeather.main_JSON);
        Main main = new Main();
        main.setTemp(mainJson.getDouble(Main.temp_JSON));
        main.setTempMin(mainJson.getDouble("temp_min"));
        main.setTempMax(mainJson.getDouble("temp_max"));
        main.setPressure(mainJson.getDouble("pressure"));
        main.setSeaLevel(mainJson.getDouble("sea_level"));
        main.setGrndLevel(mainJson.getDouble("grnd_level"));
        main.setHumidity(mainJson.getLong("humidity"));
        weatherJson.setMain(main);

        //sys
        JSONObject sysJson = json.getJSONObject(JsonWeather.sys_JSON);
        Sys sys = new Sys();
        sys.setMessage(sysJson.getDouble(Sys.message_JSON));
        sys.setCountry(sysJson.getString(Sys.country_JSON));
        sys.setSunrise(sysJson.getLong(Sys.sunrise_JSON));
        sys.setSunset(sysJson.getLong(Sys.sunset_JSON));
        weatherJson.setSys(sys);

        //weather
        JSONArray weatherJsonArray = json.getJSONArray(JsonWeather.weather_JSON);
        List<Weather> list = new ArrayList<>();

        for (int i = 0; i < weatherJsonArray.length(); i++) {
            JSONObject row = weatherJsonArray.getJSONObject(i);
            Weather weatherOb = new Weather();
            weatherOb.setId(row.getLong("id"));
            weatherOb.setMain(row.getString("main"));
            weatherOb.setDescription(row.getString("description"));
            weatherOb.setIcon(row.getString("icon"));
            list.add(weatherOb);
        }
        weatherJson.setWeather(list);

        return weatherJson;

    }


    /**
     * Parse a Json stream and return a List of Weather objects.
     */
    public List<Weather> parseWeathers(JsonReader reader) throws IOException {
        // TODO -- you fill in here.
        reader.beginArray();

        List<Weather> list = new ArrayList<>();

            while (reader.hasNext()) {
                list.add(parseWeather(reader));

             }
        reader.endArray();

    return list;
    }

    /**
     * Parse a Json stream and return a Weather object.
     */
    public Weather parseWeather(JsonReader reader) throws IOException {
        // TODO -- you fill in here.
        reader.beginObject();

        Weather weather = new Weather();
        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case Weather.id_JSON:
                        weather.setId(reader.nextLong());
                        break;
                    case Weather.main_JSON:
                        weather.setMain(reader.nextString());
                        break;
                    case Weather.description_JSON:
                        weather.setDescription(reader.nextString());
                        break;
                    case Weather.icon_JSON:
                        weather.setIcon(reader.nextString());
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
        } finally {
            reader.endObject();
        }
        return weather;
    }
    
    /**
     * Parse a Json stream and return a Main Object.
     */
    public Main parseMain(JsonReader reader) 
        throws IOException {
        reader.beginObject();
        Main main = new Main();
        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case Main.temp_JSON:
                        main.setTemp(reader.nextDouble());
                        break;
                    case Main.tempMin_JSON:
                        main.setTempMin(reader.nextDouble());
                        break;
                    case Main.tempMax_JSON:
                        main.setTempMax(reader.nextDouble());
                        break;
                    case Main.pressure_JSON:
                        main.setPressure(reader.nextDouble());
                        break;
                    case Main.seaLevel_JSON:
                        main.setSeaLevel(reader.nextDouble());
                        break;
                    case Main.grndLevel_JSON:
                        main.setGrndLevel(reader.nextDouble());
                        break;
                    case Main.humidity_JSON:
                        main.setHumidity(reader.nextLong());
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
        } finally {
            reader.endObject();
        }
        return main;

    }

    /**
     * Parse a Json stream and return a Wind Object.
     */
    public Wind parseWind(JsonReader reader) throws IOException {
        reader.beginObject();
        Wind wind = new Wind();
        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case Wind.speed_JSON:
                        wind.setSpeed(reader.nextDouble());
                        break;
                    case Wind.deg_JSON:
                        wind.setDeg(reader.nextDouble());
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
        } finally {
            reader.endObject();
        }
        return wind;
    }

    /**
     * Parse a Json stream and return a Sys Object.
     */
    public Sys parseSys(JsonReader reader) throws IOException {
    reader.beginObject();
    Sys sys = new Sys();
    try {
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case Sys.message_JSON:
                    sys.setMessage(reader.nextDouble());
                    break;
                case Sys.country_JSON:
                    sys.setCountry(reader.nextString());
                    break;
                case Sys.sunrise_JSON:
                    sys.setSunrise(reader.nextLong());
                    break;
                case Sys.sunset_JSON:
                    sys.setSunset(reader.nextLong());
                default:
                    reader.skipValue();
                    break;
            }
        }
    } finally {
        reader.endObject();
    }
        return sys;
    }
}
