/**
 * 
 */
package com.armedia.acm.form.project.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.armedia.acm.form.config.xml.OwningGroupItem;
import com.armedia.acm.form.config.xml.ParticipantItem;
import com.armedia.acm.form.project.model.xml.ProjectApprover;
import com.armedia.acm.form.project.model.xml.ProjectMilestone;
import com.armedia.acm.form.project.model.xml.ProjectReview;
import com.armedia.acm.form.project.model.xml.ProjectValue;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormNamespace;
import com.armedia.acm.frevvo.model.FrevvoForm;
import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;

/**
 * @author riste.tutureski
 *
 */
@XmlRootElement(name="form_" + FrevvoFormName.PROJECT, namespace=FrevvoFormNamespace.PROJECT_NAMESPACE)
public class ProjectForm extends FrevvoForm {

	private Long id;
	private String projectTitle;
	
	private String agencyName;
	private Date date;
	private String agencyContactName;
	private String agencyContactPhone;
	private String agencyContactEmail;
	
	private String sectionOneQuestion1;
	private String sectionOneCost1;
	private String sectionOneCost2;
	private String sectionOneExplain;
	private String sectionOneQuestion2;
	
	private String sectionTwoProblem;
	private String sectionTwoSolution;
	private String sectionTwoBenefits;
	private String sectionTwoExplain;
	
	private String sectionThreeSolution;
	private String sectionThreeEnvironment;
	private String sectionThreeProcess;
	
	private Date sectionFourProjectStartDate;
	private Date sectionFourProjectEndDate;
	private List<ProjectMilestone> sectionFourTable;
	private String sectionFourRoles;
	
	private List<ProjectReview> sectionFiveTable1;
	private List<ProjectValue> sectionFiveTable2;
	private List<ProjectApprover> sectionFiveTable3;
	
	private String sectionSixProtectedData;
	private String sectionSixAttach1Check;
	private String sectionSixAttach1Text;
	private String sectionSixAttach2Check;
	private String sectionSixAttach2Text;
	
	private String sectionSevenInfo;
	
	private String cmisFolderId;
	private List<ParticipantItem> participants;
	private List<String> participantsTypeOptions;
	private Map<String, String> participantsPrivilegeTypes;
	private OwningGroupItem owningGroup;
	private List<String> owningGroupOptions;
	
	@XmlElement(name="projectId")
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@XmlElement(name="projectTitle")
	public String getProjectTitle() {
		return projectTitle;
	}

	public void setProjectTitle(String projectTitle) {
		this.projectTitle = projectTitle;
	}

	@XmlElement(name="agencyName")
	public String getAgencyName() {
		return agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	@XmlElement(name="date")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@XmlElement(name="agencyContactName")
	public String getAgencyContactName() {
		return agencyContactName;
	}
	
	public void setAgencyContactName(String agencyContactName) {
		this.agencyContactName = agencyContactName;
	}

	@XmlElement(name="agencyContactPhone")
	public String getAgencyContactPhone() {
		return agencyContactPhone;
	}

	public void setAgencyContactPhone(String agencyContactPhone) {
		this.agencyContactPhone = agencyContactPhone;
	}

	@XmlElement(name="agencyContactEmail")
	public String getAgencyContactEmail() {
		return agencyContactEmail;
	}

	public void setAgencyContactEmail(String agencyContactEmail) {
		this.agencyContactEmail = agencyContactEmail;
	}

	@XmlElement(name="sectionOneQuestion1")
	public String getSectionOneQuestion1() {
		return sectionOneQuestion1;
	}

	public void setSectionOneQuestion1(String sectionOneQuestion1) {
		this.sectionOneQuestion1 = sectionOneQuestion1;
	}

	@XmlElement(name="sectionOneCost1")
	public String getSectionOneCost1() {
		return sectionOneCost1;
	}

	public void setSectionOneCost1(String sectionOneCost1) {
		this.sectionOneCost1 = sectionOneCost1;
	}

	@XmlElement(name="sectionOneCost2")
	public String getSectionOneCost2() {
		return sectionOneCost2;
	}

	public void setSectionOneCost2(String sectionOneCost2) {
		this.sectionOneCost2 = sectionOneCost2;
	}

	@XmlElement(name="sectionOneExplain")
	public String getSectionOneExplain() {
		return sectionOneExplain;
	}

	public void setSectionOneExplain(String sectionOneExplain) {
		this.sectionOneExplain = sectionOneExplain;
	}

	@XmlElement(name="sectionOneQuestion2")
	public String getSectionOneQuestion2() {
		return sectionOneQuestion2;
	}

	public void setSectionOneQuestion2(String sectionOneQuestion2) {
		this.sectionOneQuestion2 = sectionOneQuestion2;
	}

	@XmlElement(name="sectionTwoProblem")
	public String getSectionTwoProblem() {
		return sectionTwoProblem;
	}

	public void setSectionTwoProblem(String sectionTwoProblem) {
		this.sectionTwoProblem = sectionTwoProblem;
	}

	@XmlElement(name="sectionTwoSolution")
	public String getSectionTwoSolution() {
		return sectionTwoSolution;
	}

	public void setSectionTwoSolution(String sectionTwoSolution) {
		this.sectionTwoSolution = sectionTwoSolution;
	}

	@XmlElement(name="sectionTwoBenefits")
	public String getSectionTwoBenefits() {
		return sectionTwoBenefits;
	}

	public void setSectionTwoBenefits(String sectionTwoBenefits) {
		this.sectionTwoBenefits = sectionTwoBenefits;
	}

	@XmlElement(name="sectionTwoExplain")
	public String getSectionTwoExplain() {
		return sectionTwoExplain;
	}

	public void setSectionTwoExplain(String sectionTwoExplain) {
		this.sectionTwoExplain = sectionTwoExplain;
	}

	@XmlElement(name="sectionThreeSolution")
	public String getSectionThreeSolution() {
		return sectionThreeSolution;
	}

	public void setSectionThreeSolution(String sectionThreeSolution) {
		this.sectionThreeSolution = sectionThreeSolution;
	}

	@XmlElement(name="sectionThreeEnvironment")
	public String getSectionThreeEnvironment() {
		return sectionThreeEnvironment;
	}

	public void setSectionThreeEnvironment(String sectionThreeEnvironment) {
		this.sectionThreeEnvironment = sectionThreeEnvironment;
	}

	@XmlElement(name="sectionThreeProcess")
	public String getSectionThreeProcess() {
		return sectionThreeProcess;
	}

	public void setSectionThreeProcess(String sectionThreeProcess) {
		this.sectionThreeProcess = sectionThreeProcess;
	}

	@XmlElement(name="sectionFourProjectStartDate")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	public Date getSectionFourProjectStartDate() {
		return sectionFourProjectStartDate;
	}

	public void setSectionFourProjectStartDate(Date sectionFourProjectStartDate) {
		this.sectionFourProjectStartDate = sectionFourProjectStartDate;
	}

	@XmlElement(name="sectionFourProjectEndDate")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	public Date getSectionFourProjectEndDate() {
		return sectionFourProjectEndDate;
	}

	public void setSectionFourProjectEndDate(Date sectionFourProjectEndDate) {
		this.sectionFourProjectEndDate = sectionFourProjectEndDate;
	}

	@XmlElement(name="sectionFourTableItem")
	public List<ProjectMilestone> getSectionFourTable() {
		return sectionFourTable;
	}

	public void setSectionFourTable(List<ProjectMilestone> sectionFourTable) {
		this.sectionFourTable = sectionFourTable;
	}

	@XmlElement(name="sectionFourRoles")
	public String getSectionFourRoles() {
		return sectionFourRoles;
	}

	public void setSectionFourRoles(String sectionFourRoles) {
		this.sectionFourRoles = sectionFourRoles;
	}

	@XmlElement(name="sectionFiveTable1Item")
	public List<ProjectReview> getSectionFiveTable1() {
		return sectionFiveTable1;
	}

	public void setSectionFiveTable1(List<ProjectReview> sectionFiveTable1) {
		this.sectionFiveTable1 = sectionFiveTable1;
	}

	@XmlElement(name="sectionFiveTable2Item")
	public List<ProjectValue> getSectionFiveTable2() {
		return sectionFiveTable2;
	}

	public void setSectionFiveTable2(List<ProjectValue> sectionFiveTable2) {
		this.sectionFiveTable2 = sectionFiveTable2;
	}

	@XmlElement(name="sectionFiveTable3Item")
	public List<ProjectApprover> getSectionFiveTable3() {
		return sectionFiveTable3;
	}

	public void setSectionFiveTable3(List<ProjectApprover> sectionFiveTable3) {
		this.sectionFiveTable3 = sectionFiveTable3;
	}

	@XmlElement(name="sectionSixProtectedData")
	public String getSectionSixProtectedData() {
		return sectionSixProtectedData;
	}

	public void setSectionSixProtectedData(String sectionSixProtectedData) {
		this.sectionSixProtectedData = sectionSixProtectedData;
	}

	@XmlElement(name="sectionSixAttach1Check")
	public String getSectionSixAttach1Check() {
		return sectionSixAttach1Check;
	}

	public void setSectionSixAttach1Check(String sectionSixAttach1Check) {
		this.sectionSixAttach1Check = sectionSixAttach1Check;
	}

	@XmlElement(name="sectionSixAttach1Text")
	public String getSectionSixAttach1Text() {
		return sectionSixAttach1Text;
	}

	public void setSectionSixAttach1Text(String sectionSixAttach1Text) {
		this.sectionSixAttach1Text = sectionSixAttach1Text;
	}

	@XmlElement(name="sectionSixAttach2Check")
	public String getSectionSixAttach2Check() {
		return sectionSixAttach2Check;
	}

	public void setSectionSixAttach2Check(String sectionSixAttach2Check) {
		this.sectionSixAttach2Check = sectionSixAttach2Check;
	}

	@XmlElement(name="sectionSixAttach2Text")
	public String getSectionSixAttach2Text() {
		return sectionSixAttach2Text;
	}

	public void setSectionSixAttach2Text(String sectionSixAttach2Text) {
		this.sectionSixAttach2Text = sectionSixAttach2Text;
	}

	@XmlElement(name="sectionSevenInfo")
	public String getSectionSevenInfo() {
		return sectionSevenInfo;
	}

	public void setSectionSevenInfo(String sectionSevenInfo) {
		this.sectionSevenInfo = sectionSevenInfo;
	}

	@XmlTransient
	public String getCmisFolderId() {
		return cmisFolderId;
	}

	public void setCmisFolderId(String cmisFolderId) {
		this.cmisFolderId = cmisFolderId;
	}

	@XmlElement(name="participantsItem", type=ParticipantItem.class)
	public List<ParticipantItem> getParticipants() {
		return participants;
	}

	public void setParticipants(List<ParticipantItem> participants) {
		this.participants = participants;
	}

	@XmlTransient
	public List<String> getParticipantsTypeOptions() {
		return participantsTypeOptions;
	}

	public void setParticipantsTypeOptions(List<String> participantsTypeOptions) {
		this.participantsTypeOptions = participantsTypeOptions;
	}

	@XmlTransient
	public Map<String, String> getParticipantsPrivilegeTypes() {
		return participantsPrivilegeTypes;
	}

	public void setParticipantsPrivilegeTypes(
			Map<String, String> participantsPrivilegeTypes) {
		this.participantsPrivilegeTypes = participantsPrivilegeTypes;
	}

	@XmlElement(name="owningGroup")
	public OwningGroupItem getOwningGroup() {
		return owningGroup;
	}

	public void setOwningGroup(OwningGroupItem owningGroup) {
		this.owningGroup = owningGroup;
	}

	@XmlTransient
	public List<String> getOwningGroupOptions() {
		return owningGroupOptions;
	}

	public void setOwningGroupOptions(List<String> owningGroupOptions) {
		this.owningGroupOptions = owningGroupOptions;
	}
}
