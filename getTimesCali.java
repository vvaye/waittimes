import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimerTask;
import java.util.Timer;

public class GetTimesCali {

    public static void main(String[] args) {
        int minutes = 5; // The delay in minutes
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                getTimes();
            }
        }, 0, 1000 * 60 * minutes);
    }

    public static void getTimes() {
        String waitTime = "";
        String rideType = "";
        String rideName = "";
        String[] rideTimes = new String[10];
        String[] wantedRides = new String[] {"RadiatorSpringsRacers","Incredicoaster","SillySymphonySwings","GuardiansoftheGalaxy-Mission:BREAKOUT!","GrizzlyRiverRun","Soarin'AroundtheWorld","ToyStoryMidwayMania!","WEBSLINGERS:ASpider-ManAdventure","PixarPal-A-Round–Non-Swinging","PixarPal-A-Round-Swinging"};


        try {
            URL url = new URL("https://api.themeparks.wiki/preview/parks/DisneylandResortCaliforniaAdventure/waittime");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE MM/dd HH:mm a");
            String fullDate = formatter.format(date);

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            FileWriter csvWriter = new FileWriter("DisneyTimesCA0613.csv", true);
            File newFile = new File("DisneyTimesCA0613.csv");
            if (newFile.length() == 0) {
                csvWriter.append("Date,Radiator Springs Racers,Incredicoaster,Silly Symphony Swings,Guardians of the Galaxy-Mission: BREAKOUT!,Grizzly River Run,Soarin' Around the World,Toy Story Midway Mania!,WEBSLINGERS: A Spider-Man Adventure,Pixar Pal-A-Round–Non-Swinging,Pixar Pal-A-Round-Swinging,\n");
            }

            String output;
            while ((output = br.readLine()) != null) {

                output = output.replaceAll("\\s", "");
                output = output.replaceAll("\"", "");
                output = output.replaceAll(",", "");

                if (output.substring(0,1).equals("w")) {
                    if (output.substring(9).equals("null")) {
                        waitTime = "closed";
                    } else {
                        waitTime = output.substring(9) + " minutes";
                    }
                } else if (output.substring(0,1).equals("n")) {
                    rideName = output.substring(5);
                } else if (output.substring(0,1).equals("t")) {
                    rideType = output.substring(5);
                }

                if (rideType.equals("ATTRACTION") && isWanted(rideName, wantedRides)) {
                    for (int i = 0; i < wantedRides.length; i++) {
                        if (rideName.equals(wantedRides[i])) {
                            rideTimes[i] = waitTime;
                        }
                    }
                    rideType = "";
                }

            }

            csvWriter.write(fullDate + ",");
            for (int i = 0; i < rideTimes.length; i ++) {
                csvWriter.write(rideTimes[i] + ",");
            }
            csvWriter.write("\n");
            csvWriter.flush();
            csvWriter.close();
            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    public static boolean isWanted(String ride, String[] wantedRides) {
        for (int i = 0; i < wantedRides.length; i++) {
            if (ride.equals(wantedRides[i])) {
                return true;
            }
        }
        return false;
    }

}
