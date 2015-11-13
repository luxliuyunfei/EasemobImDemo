package com.sinohealth.hximlibrary.controller;

import com.easemob.chat.EMMessage;
import com.sinohealth.hximlibrary.model.DefaultLibModel;
import com.sinohealth.hximlibrary.model.HXSDKModel;
import com.sinohealth.hximlibrary.utils.Constant;

import org.json.JSONObject;

/**
 * Created by JJfly on 2015/11/11.
 */
public class ImLibHelper extends HXSDKHelper {
    @Override
    protected HXSDKModel createModel() {
        return new DefaultLibModel(appContext);
    }
    public boolean isRobotMenuMessage(EMMessage message) {

        try {
            JSONObject jsonObj = message.getJSONObjectAttribute(Constant.MESSAGE_ATTR_ROBOT_MSGTYPE);
            if (jsonObj.has("choice")) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }
}
