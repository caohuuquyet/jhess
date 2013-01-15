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
        <link href="http://localhost:8088/jHESS/view/images/images/style.css" rel="stylesheet" type="text/css" />
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
                    <img src="images/logo.jpg" border="0" align="center"/>                
			    </div>
                <div class="hkhboxmenu">				
                    <ul id="verticalmenu" class="glossymenu">                          
                            <li> <a href="#"> Approach 1</a> </li>
							<li> <a href="#"> Approach 2</a> </li>
							<li> <a href="#"> Approach 3</a> </li>	
                    </ul>
                </div>                                             
            </div>

            <div class="rightside">
				<img src="images/topline.jpg" border="0" style="margin-top:10px;" align="right"/>
				<div class="clearfloat"></div>
				<div class="servicelist">
					<c:forEach var="device" items="${devices}">
						<div class="newsitem" >
							<a href="#">
								<img src="images/devices/${device.id}${device.currentDeviceStatus}.JPG" alt="jHESS " width="113" height="78" class="thumb" border="0" />
							</a>
							<span class="title"> ${device.location}  </span> <br /> ${device.inputPower} ${device.inputPowerUnit} <br/>
						</div>						
					</c:forEach>			
					
				</div>
			</div>			
			<div class="clearfloat"></div>
            <center>
                <img src="images/bottomline.jpg" border="0" alt="jHess, Hybrid Energy Saving Service" /> <br/>
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

