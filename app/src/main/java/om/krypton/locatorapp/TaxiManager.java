package om.krypton.locatorapp;

import android.location.Location;

public class TaxiManager {

    private Location destinationLocation;

    public void setDestinationLocation(Location destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public float returnTheDistanceToDestinationLocationInMeters(Location currentLocation){
        if (currentLocation != null && destinationLocation != null){
            return currentLocation.distanceTo(destinationLocation);
        } else {
            return -100.0f;
        }
    }

    public String returnTheMilesBewtweenCurrentLocationAndDestinationLocation(Location currentLocation, int metersPerMile){
        int miles = (int) (returnTheDistanceToDestinationLocationInMeters(currentLocation)/metersPerMile);

        if(miles == 1){
            return  "1 Mile";
        } else if(miles > 1){
            return "Miles " + miles;
        } else {
            return "No Mile";
        }
    }

    public String returnTheTimeLeftToGetToDestinationLocation(Location currentLocation, float milesPerHour, int meteresPerMile){
        float distanceInMeters = returnTheDistanceToDestinationLocationInMeters(currentLocation);
        float timeLeft = distanceInMeters / (milesPerHour * meteresPerMile);

        String timeResult = "";

        int timeLeftInHours = (int) timeLeft;

        if (timeLeftInHours == 1){
            timeResult = timeResult + "1 Hour";
        } else if (timeLeftInHours > 1){
            timeResult += timeLeftInHours +" Hours ";
        }

        int minutesLeft = (int)((timeLeft - timeLeftInHours) * 60);

        if (minutesLeft == 1){
            timeResult+= "1 minute";
        } else if (minutesLeft > 1){
            timeResult += minutesLeft + " minutes ";
        }

        if (timeLeftInHours < 0 && minutesLeft < 0){
            timeResult = "Less than a minute to reach destination";
        }

        return timeResult;
    }
}
