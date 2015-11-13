package com.sinohealth.hximlibrary.proxy;

import com.sinohealth.hximlibrary.proxy.interfaces.LoginSubject;

/**
 * Created by JJfly on 2015/11/12.
 */
public class LoginProxy implements LoginSubject{

    private LoginSubject loginSubject;

    public LoginProxy(LoginSubject loginSubject) {
        this.loginSubject = loginSubject;
    }

    @Override
    public void doLogin() {

        loginSubject.doLogin();

    }
}
