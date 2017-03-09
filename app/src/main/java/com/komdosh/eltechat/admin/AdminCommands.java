package com.komdosh.eltechat.admin;

import com.komdosh.eltechat.constants.JSONConstants;
import com.komdosh.eltechat.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by komdosh on 05.03.17.
 */

public class AdminCommands {
    public static String kick(String name){
        Utils utils = Utils.getInstance();
        Map<String, String> keyVal = new HashMap<>();
        keyVal.put(JSONConstants.FLAG, AdminCommandConstants.KICK);
        keyVal.put(JSONConstants.NAME, name);

        return utils.getJSON(keyVal);
    }

    public static String mute(String name){
        Utils utils = Utils.getInstance();
        Map<String, String> keyVal = new HashMap<>();
        keyVal.put(JSONConstants.FLAG, AdminCommandConstants.MUTE);
        keyVal.put(JSONConstants.NAME, name);

        return utils.getJSON(keyVal);
    }

    public static String unmute(String name){
        Utils utils = Utils.getInstance();
        Map<String, String> keyVal = new HashMap<>();
        keyVal.put(JSONConstants.FLAG, AdminCommandConstants.UNMUTE);
        keyVal.put(JSONConstants.NAME, name);

        return utils.getJSON(keyVal);
    }

    public static String deleteMessage(Long messageId){
        Utils utils = Utils.getInstance();
        Map<String, String> keyVal = new HashMap<>();
        keyVal.put(JSONConstants.FLAG, AdminCommandConstants.DELETE_MESSAGE);
        keyVal.put(JSONConstants.MESSAGE_ID, messageId.toString());

        return utils.getJSON(keyVal);
    }
}
