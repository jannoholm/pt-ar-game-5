package com.playtech.ptargame5.web.model;

public class PlayerInterests {

	private Boolean consent;
	private Boolean development;
	private Boolean projectManagement;
	private Boolean analysis;
	private Boolean devOps;
	private Boolean infra;
	private Boolean qa;
	private Boolean adminHr;

	public Boolean getConsent() {
		return consent;
	}

	public void setConsent(Boolean consent) {
		this.consent = consent;
	}

	public Boolean getDevelopment() {
		return development;
	}

	public void setDevelopment(Boolean development) {
		this.development = development;
	}

	public Boolean getProjectManagement() {
		return projectManagement;
	}

	public void setProjectManagement(Boolean projectManagement) {
		this.projectManagement = projectManagement;
	}

	public Boolean getAnalysis() {
		return analysis;
	}

	public void setAnalysis(Boolean analysis) {
		this.analysis = analysis;
	}

	public Boolean getDevOps() {
		return devOps;
	}

	public void setDevOps(Boolean devOps) {
		this.devOps = devOps;
	}

	public Boolean getInfra() {
		return infra;
	}

	public void setInfra(Boolean infra) {
		this.infra = infra;
	}

	public Boolean getQa() {
		return qa;
	}

	public void setQa(Boolean qa) {
		this.qa = qa;
	}

	public Boolean getAdminHr() {
		return adminHr;
	}

	public void setAdminHr(Boolean adminHr) {
		this.adminHr = adminHr;
	}

	@Override
	public String toString() {
		return "PlayerInterests [consent=" + consent + ", development=" + development + ", projectManagement="
				+ projectManagement + ", analysis=" + analysis + ", devOps=" + devOps + ", infra=" + infra + ", qa="
				+ qa + ", adminHr=" + adminHr + "]";
	}
}
