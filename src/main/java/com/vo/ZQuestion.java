package com.vo;

import java.util.ArrayList;
import java.util.List;

public class ZQuestion implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String questionType = "";
	private String title = "";
	private List<String> options = new ArrayList<>();
	private String ans = "";
	private String  analysis = "";
	private List<ZQuestion> sonQuestions = new ArrayList<>();

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public String getAns() {
		return ans;
	}

	public void setAns(String ans) {
		this.ans = ans;
	}

	public String getAnalysis() {
		return analysis;
	}

	public void setAnalysis(String analysis) {
		this.analysis = analysis;
	}

	public List<ZQuestion> getSonQuestions() {
		return sonQuestions;
	}

	public void setSonQuestions(List<ZQuestion> sonQuestions) {
		this.sonQuestions = sonQuestions;
	}

	@Override
	public String toString() {
		return "ZQuestion{" +
				"questionType='" + questionType + '\'' +
				", title='" + title + '\'' +
				", options=" + options +
				", ans='" + ans + '\'' +
				", analysis='" + analysis + '\'' +
				", sonQuestions=" + sonQuestions +
				'}';
	}
}

