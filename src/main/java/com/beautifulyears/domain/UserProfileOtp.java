package com.beautifulyears.domain;

import java.lang.reflect.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileOtp extends UserProfile {

	private String otp;

	@JsonProperty
	public String getOtp(){
		return otp;
	}

	@JsonIgnore
	public void setOtp(String otp){
		this.otp = otp;
	}

	public UserProfile getUserProfileObj(){
		UserProfileOtp source = this;
		UserProfile target = new UserProfile();
		Field[] fieldsSource = source.getClass().getSuperclass().getDeclaredFields();
		Field[] fieldsTarget = target.getClass().getDeclaredFields();
		
		for (Field fieldTarget : fieldsTarget)
        {
            for (Field fieldSource : fieldsSource)
            {
                if (fieldTarget.getName().equals(fieldSource.getName()))
                {
                    try
                    {
						fieldSource.setAccessible(true);
						fieldTarget.setAccessible(true);
                        fieldTarget.set(target, fieldSource.get(source));
                    }
                    catch (SecurityException e)
                    {
                    }
                    catch (IllegalArgumentException e)
                    {
                    }
                    catch (IllegalAccessException e)
                    {
                    }
                    break;
                }
            }
		}
		return target;
	}
}
