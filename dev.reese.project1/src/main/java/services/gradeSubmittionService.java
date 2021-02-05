package services;

import java.util.List;

import daos.DBImpGradeNoteDAO;
import daos.DBImpSubmittedGradeDAO;
import daos.DBImpTuitionReimbursementRequestDAO;
import daos.GradeNoteDAO;
import daos.SubmittedGradeDAO;
import daos.TuitionReimbursementRequestDAO;
import dev.reese.project1.entities.GradeNote;
import dev.reese.project1.entities.SubmittedGrade;
import dev.reese.project1.entities.TuitionReimbursementRequest;
import serviceexceptions.RequestNotFoundException;

public class gradeSubmittionService {
	
	private SubmittedGradeDAO sgDAO = new DBImpSubmittedGradeDAO();
	private GradeNoteDAO gnDAO = new DBImpGradeNoteDAO();
	private TuitionReimbursementRequestDAO trrDAO = new DBImpTuitionReimbursementRequestDAO();
	
	public SubmittedGrade submitGrade(SubmittedGrade sg) {
		sg.setSubmittedAt(new java.sql.Timestamp(new java.util.Date().getTime()));
		this.sgDAO.createSubmittedGrade(sg);
		return this.sgDAO.getRequestsSubmittedGrade(sg.getRequestId());
	}
	
	
	public GradeNote attachNoteToGrade(SubmittedGrade sg, GradeNote gn) {
		gn.setAddedAt(new java.sql.Timestamp(new java.util.Date().getTime()));
		gn.setGradeSubmissionId(sg.getId());
		this.gnDAO.createGradeNote(gn);
		return this.gnDAO.getGradeNote(gn.getGradeSubmissionId(), gn.getAddedAt());
	}
	
	
	public SubmittedGrade retrieveRequestsSubmittedGradeNoAttachments(int requestId) throws RequestNotFoundException {
		TuitionReimbursementRequest trr = this.trrDAO.getTuitionReimbursementRequest(requestId);
		if(trr.getId() == 0)
			throw new RequestNotFoundException("the request ID entered does not exist in the system");
		return this.sgDAO.getRequestsSubmittedGrade(requestId);
	}
	
	public SubmittedGrade retrieveRequestsSubmittedGradeWithAttachments(int requestId) throws RequestNotFoundException {
		TuitionReimbursementRequest trr = this.trrDAO.getTuitionReimbursementRequest(requestId);
		if(trr.getId() == 0)
			throw new RequestNotFoundException("the request ID entered does not exist in the system");
		SubmittedGrade sg = this.sgDAO.getRequestsSubmittedGrade(requestId);
		sg.setNotes(this.gnDAO.getAllNotesForGrade(sg.getId()));
		return sg;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	public SubmittedGrade MarkSubmittionAsReviewedAndPassed(int gradeId, int reviewedById) throws RequestNotFoundException {
		SubmittedGrade sg = this.sgDAO.getSubmittedGrade(reviewedById);
		if(sg.getId() == 0)
			throw new RequestNotFoundException("that graade ID does not exist in the system.");
		sg.setReviewedBy(reviewedById);
		sg.setPassed(true);
		sg.setReviewedAt(new java.sql.Timestamp(new java.util.Date().getTime()));
		this.sgDAO.updateSubmittedGrade(sg);
		return this.sgDAO.getSubmittedGrade(gradeId);
	}
	
	public SubmittedGrade MarkSubmittionAsReviewedAndFailed(int gradeId, int reviewedById) throws RequestNotFoundException {
		SubmittedGrade sg = this.sgDAO.getSubmittedGrade(reviewedById);
		if(sg.getId() == 0)
			throw new RequestNotFoundException("that graade ID does not exist in the system.");
		sg.setReviewedBy(reviewedById);
		sg.setPassed(false);
		sg.setReviewedAt(new java.sql.Timestamp(new java.util.Date().getTime()));
		this.sgDAO.updateSubmittedGrade(sg);
		return this.sgDAO.getSubmittedGrade(gradeId);
	}
	
	
	

}
