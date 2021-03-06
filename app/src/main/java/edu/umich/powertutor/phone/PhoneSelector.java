/*
Copyright (C) 2011 The University of Michigan

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

Please send inquiries to powertutor@umich.edu
*/

package edu.umich.powertutor.phone;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.List;

import edu.umich.powertutor.components.Audio;
import edu.umich.powertutor.components.Audio.AudioData;
import edu.umich.powertutor.components.CPU;
import edu.umich.powertutor.components.CPU.CpuData;
import edu.umich.powertutor.components.GPS;
import edu.umich.powertutor.components.GPS.GpsData;
import edu.umich.powertutor.components.LCD;
import edu.umich.powertutor.components.LCD.LcdData;
import edu.umich.powertutor.components.OLED;
import edu.umich.powertutor.components.OLED.OledData;
import edu.umich.powertutor.components.PowerComponent;
import edu.umich.powertutor.components.Sensors;
import edu.umich.powertutor.components.Sensors.SensorData;
import edu.umich.powertutor.components.Threeg;
import edu.umich.powertutor.components.Threeg.ThreegData;
import edu.umich.powertutor.components.Wifi;
import edu.umich.powertutor.components.Wifi.WifiData;
import edu.umich.powertutor.service.PowerData;
import edu.umich.powertutor.util.NotificationService;
import edu.umich.powertutor.util.SystemInfo;

public class PhoneSelector {
    private static final String TAG = "PhoneSelector";

    public static final int PHONE_UNKNOWN = 0;

    /* A hard-coded list of phones that have OLED screens. */
    public static final String[] OLED_PHONES = {
            "bravo",
            "passion",
            "GT-I9000",
            "inc",
            "legend",
            "GT-I7500",
            "SPH-M900",
            "SGH-I897",
            "SGH-T959",
            "desirec",
    };


    /* This class is not supposed to be instantiated.  Just use the static
     * members.
     */
    private PhoneSelector() {
    }

    public static boolean phoneSupported() {
        return getPhoneType() != PHONE_UNKNOWN;
    }

    public static boolean hasOled() {
        for (int i = 0; i < OLED_PHONES.length; i++) {
            if (Build.DEVICE.equals(OLED_PHONES[i])) {
                return true;
            }
        }
        return false;
    }

    public static int getPhoneType() {
        return PHONE_UNKNOWN;
    }

    public static PhoneConstants getConstants(Context context) {
        boolean oled = hasOled();
        return oled ? new OledConstants(context) :
                new DreamConstants(context);
    }

    public static PhonePowerCalculator getCalculator(Context context) {
        boolean oled = hasOled();
        return oled ? new OledPowerCalculator(context) :
                new DreamPowerCalculator(context);
    }

    public static void generateComponents(Context context,
                                          List<PowerComponent> components,
                                          List<PowerFunction> functions) {
        final PhoneConstants constants = getConstants(context);
        final PhonePowerCalculator calculator = getCalculator(context);

        //TODO: What about bluetooth?
        //TODO: LED light on the Nexus

    /* Add display component. */
        if (hasOled()) {
            components.add(new OLED(context, constants));
            functions.add(new PowerFunction() {
                public double calculate(PowerData data) {
                    return calculator.getOledPower((OledData) data);
                }
            });
        } else {
            components.add(new LCD(context));
            functions.add(new PowerFunction() {
                public double calculate(PowerData data) {
                    return calculator.getLcdPower((LcdData) data);
                }
            });
        }

    /* Add CPU component. */
        components.add(new CPU(constants));
        functions.add(new PowerFunction() {
            public double calculate(PowerData data) {
                return calculator.getCpuPower((CpuData) data);
            }
        });

    /* Add Wifi component. */
        String wifiInterface =
                SystemInfo.getInstance().getProperty("wifi.interface");
        if (wifiInterface != null && wifiInterface.length() != 0) {
            components.add(new Wifi(context, constants));
            functions.add(new PowerFunction() {
                public double calculate(PowerData data) {
                    return calculator.getWifiPower((WifiData) data);
                }
            });
        }

    /* Add 3G component. */
        if (constants.threegInterface().length() != 0) {
            components.add(new Threeg(context, constants));
            functions.add(new PowerFunction() {
                public double calculate(PowerData data) {
                    return calculator.getThreeGPower((ThreegData) data);
                }
            });
        }

    /* Add GPS component. */
        components.add(new GPS(context, constants));
        functions.add(new PowerFunction() {
            public double calculate(PowerData data) {
                return calculator.getGpsPower((GpsData) data);
            }
        });

    /* Add Audio component. */
        components.add(new Audio(context));
        functions.add(new PowerFunction() {
            public double calculate(PowerData data) {
                return calculator.getAudioPower((AudioData) data);
            }
        });

    /* Add Sensors component if avaialble. */
        if (NotificationService.available()) {
            components.add(new Sensors(context));
            functions.add(new PowerFunction() {
                public double calculate(PowerData data) {
                    return calculator.getSensorPower((SensorData) data);
                }
            });
        }
    }
}
