///**
// * 
// */
//package com.fosun.fc.projects.creepers.proxy;
//
//import java.io.IOException;
//
//import org.apache.http.HttpException;
//import org.apache.http.HttpStatus;
//import org.apache.http.auth.AuthScope;
//import org.apache.http.auth.Credentials;
//import org.apache.http.auth.UsernamePasswordCredentials;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.params.CookiePolicy;
//import org.hibernate.validator.internal.util.privilegedactions.GetMethod;
//
///**   
//*    
//* 描述：   代理服务器IP以及端口
//* 创建人：pengyk   
//* 创建时间：2016年12月9日 上午9:52:06
//*    
//*/
//public class ProxyServer {
//    public static String proxyIP [] = {"ec2-23-22-95-3.compute-1.amazonaws.com", 
//            "211.68.70.169", "202.203.132.29",
//            "218.192.175.84", "ec2-50-16-197-120.compute-1.amazonaws.com", 
//            "50.22.206.184-static.reverse.softlayer.com",};
//    public static int proxyPort[] = {8000, 3128, 3128, 3128, 8001, 8080};
//    HttpClient httpClient = new HttpClient();
//    // 设置 Http 连接超时 5s
//    httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
//    /**
//     * 设置代理
//    */
//    private void setProxy(String proxyIP, int hostPort) {
//        System.out.println("正在设置代理：" + proxyIP + ":" + hostPort);
//        // TODO Auto-generated method stub
//        httpClient.getHostConfiguration().setHost(Hosturl, hostPort, "http");
//        httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
//        httpClient.getHostConfiguration().setProxy(proxyIP, proxyPort);
//            Credentials defaultcreds = new UsernamePasswordCredentials("",  "");
//            httpClient.getState().setProxyCredentials(
//                new AuthScope(proxyIP, proxyPort, null), defaultcreds);
//    }
//    
//    /**
//    * @param string
//    * @param i
//    *
//    * 测试代理服务器的可用性
//    *
//    * 只有返回HttpStatus.SC_OK才说明代理服务器有效
//    * 其他的都是不行的
//    */
//    private int testProxyServer(String url, String proxyIp, int proxyPort) {
//        // TODO Auto-generated method stub
//        setProxy(proxyIp, proxyPort);
//        GetMethod getMethod = setGetMethod(url);
//        if(getMethod == null){
//            System.out.println("请求协议设置都搞错了，所以我无法完成您的请求");
//            System.exit(1);
//        }
//        try {
//            int statusCode = httpClient.executeMethod(getMethod);
//            if (statusCode == HttpStatus.SC_OK) { //2XX状态码
//                return HttpStatus.SC_OK;
//            }else if(statusCode == HttpStatus.SC_FORBIDDEN){ //代理还是不行
//                return HttpStatus.SC_FORBIDDEN;
//            }else{  //  其他的错误
//                return 0;
//            }
//        } catch (HttpException e) {
//            // 发生致命的异常，可能是协议不对或者返回的内容有问题
//            System.out.println("Please check your provided http address!");
//            System.exit(1);
//        } catch (IOException e) {
//            // 发生网络异常
//            System.exit(1);
//        } finally {
//            // 释放连接
//            getMethod.releaseConnection();
//        }
//        return 0;
//    }
//    
//    private void test(){
//        if(statusCode == HttpStatus.SC_FORBIDDEN){  //访问被人家限制了就设置代理
//            //代理服务器的个数
//            int proxySize = proxyServer.proxyIP.length;
//            int i = 0;
//            for(; i < proxySize; i++){         //我们一个一个测试代理服务器的有用性         
//                System.out.println("正在测试代理：" + 
//                    proxyServer.proxyIP[i] + ":" + proxyServer.proxyPort[i]);         
//                int status = testProxyServer(url, proxyServer.proxyIP[i], 
//                    proxyServer.proxyPort[i]);         
//                if(status == HttpStatus.SC_OK){//代理服务器找到了             
//                    break;         
//                }else{ //其他情况你就继续去代理吧                                 
//                    continue;         
//                }
//            }
//                
//            if(i >= proxySize){
//                System.out.println("唉，我把你设置的代理服务器都测试了，
//                    好像没有发现有效的代理，我只有退出了！");
//                return null;
//            }
//         
//            System.out.println("代理：" + proxyServer.proxyIP[i] + ":" + 
//                    proxyServer.proxyPort[i] + "目前可用");
//            proxyIP = proxyServer.proxyIP[i];
//            proxyPort = proxyServer.proxyPort[i];
//    }
//}