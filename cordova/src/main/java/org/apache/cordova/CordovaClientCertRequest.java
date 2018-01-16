/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package org.apache.cordova;

import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import android.os.Build;
import android.webkit.ClientCertRequest;

/**
 * Implementation of the ICordovaClientCertRequest for Android WebView.
 */
public class CordovaClientCertRequest implements ICordovaClientCertRequest {

    private final ClientCertRequest request;

    public CordovaClientCertRequest(ClientCertRequest request) {
        this.request = request;
    }
    
    /**
     * Cancel this request
     */
    public void cancel()
    {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            request.cancel();
        }
    }
    
    /*
     * Returns the host name of the server requesting the certificate.
     */
    public String getHost()
    {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            return request.getHost();
        }
        return "";
    }
    
    /*
     * Returns the acceptable types of asymmetric keys (can be null).
     */
    public String[] getKeyTypes()
    {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            return request.getKeyTypes();
        }
        return new String[0];
    }
    
    /*
     * Returns the port number of the server requesting the certificate.
     */
    public int getPort()
    {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            return request.getPort();
        }
        return 0;
    }
    
    /*
     * Returns the acceptable certificate issuers for the certificate matching the private key (can be null).
     */
    public Principal[] getPrincipals()
    {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            return request.getPrincipals();
        }
        return null;
    }
    
    /*
     * Ignore the request for now. Do not remember user's choice.
     */
    public void ignore()
    {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            request.ignore();
        }
    }
    
    /*
     * Proceed with the specified private key and client certificate chain. Remember the user's positive choice and use it for future requests.
     * 
     * @param privateKey The privateKey
     * @param chain The certificate chain 
     */
    public void proceed(PrivateKey privateKey, X509Certificate[] chain)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            request.proceed(privateKey, chain);
        }
    }
}
