/**
 * 
 */
package com.armedia.acm.form.config.xml;

import javax.xml.bind.annotation.XmlElement;

import com.armedia.acm.services.users.model.AcmUser;

/**
 * @author riste.tutureski
 *
 */
public class ProsecutorUser extends AcmUser {

	private static final long serialVersionUID = 1L;

	private String location;
	private String phone;
	
	public ProsecutorUser()
	{

	}

	public ProsecutorUser(AcmUser user){
		setUserId(user.getUserId());
		setFirstName(user.getFirstName());
		setLastName(user.getLastName());
		setMail(user.getMail());
	}
	
	@XmlElement(name="prosecutorId")
	@Override
	public String getUserId(){
        return super.getUserId();
    }

	@Override
    public void setUserId(String userId){
        super.setUserId(userId);
    }
    
	@XmlElement(name="prosecutorFirstName")
	@Override
    public String getFirstName(){
        return super.getFirstName();
    }

	@Override
    public void setFirstName(String firstName){
        super.setFirstName(firstName);
    }

	@XmlElement(name="prosecutorLastName")
	@Override
    public String getLastName(){
        return super.getLastName();
    }

	@Override
    public void setLastName(String lastName){
        super.setLastName(lastName);
    }

	@XmlElement(name="prosecutorEmail")
	@Override
    public String getMail() {
        return super.getMail();
    }

	@Override
    public void setMail(String mail) {
        super.setMail(mail);
    }

	@XmlElement(name="prosecutorLocation")
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@XmlElement(name="prosecutorPhone")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}
