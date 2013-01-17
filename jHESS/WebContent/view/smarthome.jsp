<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
 

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>jHess, Java Hybrid Energy Saving Service</title> 
		<meta name="Keywords" content="jHess, Java Hybrid Energy Saving Service">
		<meta name="Description" content="jHess, Java Hybrid Energy Saving Service">		
        <link href="/jHESS/view/images/style.css" rel="stylesheet" type="text/css" />
    </head>
    <body >
        <div class="topmenu">
                <div class="topmenuitem">					
					<ul>   
					     
                            <li> <a href="#"> CONTACT</a> </li>							
                            <li> <a href="#"> jHESS V1.0</a> </li>
							<li>Welcome to Java Hybrid Energy Saving Service!</li>	
					</ul>
				</div>
        </div>

        <div class="container">
            <div class="leftside">
				<div class="hkhbox2">
                    <img src="/jHESS/view/images/logo.jpg" border="0" />                
			    </div>
                <div class="hkhboxmenu">				
                    <ul id="verticalmenu" class="glossymenu">                          
                            <li> <a href="/jHESS/myhome"> Approach 1</a> </li>
							<li> <a href="/jHESS/autorule"> Approach 2</a> </li>
							<li> <a href="/jHESS/learning"> Approach 3</a> </li>	
                    </ul>
                </div>                                             
            </div>

            <div class="rightside">
				<img src="/jHESS/view/images/topline.jpg" border="0" style="margin-top:10px;" align="right"/>
				<div class="clearfloat"></div>
				<div class="servicelist">
					
					<c:forEach var="device" items="${it}">
						<div class="newsitem" >
							<a href="/jHESS/myhome/${device.id}">  
								<img src="/jHESS/view/images/devices/${device.id}_${device.currentDeviceStatus}h.JPG" alt="${device.location} " width="113" height="78" class="thumb" border="0" />
							</a>
							<br/>${device.description} <br/> <span class="title"> ${device.inputPower} ${device.inputPowerUnit} </span>   
						</div>						
					</c:forEach>			
					
				</div>
			</div>			
			<div class="clearfloat"></div>
            <center>
                <img src="/jHESS/view/images/bottomline.jpg" border="0" alt="jHess, Hybrid Energy Saving Service" /> <br/>
                    <table align="center">
                    <tr>
                        <td align="center">
							<b>&copy; Copyright 2013, Java Hybrid Energy Saving Service - jHESS V1.0</b> <br/>
			            </td>
                    </tr>
                </table>
            </center>
        </div>

    </body>
</html>

