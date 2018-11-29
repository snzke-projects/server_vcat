package com.vcat.api.service;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.ActivityStatDao;
import com.vcat.module.ec.dto.QuestionDto;
import com.vcat.module.ec.entity.ActivityStat;
import com.vcat.module.ec.entity.ActivityStatAnswer;

@Service
@Transactional(readOnly = true)
public class ActivityStatService extends CrudService<ActivityStat> {
	@Autowired
	private ActivityStatDao activityStatDao;
	protected CrudDao<ActivityStat> getDao() {
		return activityStatDao;
	}
	
	public List<QuestionDto> getQuestionList(String feedBackId) {
		
		return activityStatDao.getQuestionList(feedBackId);
	}

	
	public List<QuestionDto> createPercent(List<QuestionDto> questionList) {
		NumberFormat formatter = new DecimalFormat("0");
		for (QuestionDto question : questionList) {
			List<ActivityStatAnswer> list = question.getStatAnswerList();
			if (list != null && list.size() > 0) {
				int otherPercent = 0;
				for (int i = 0; i < list.size(); i++) {
					ActivityStatAnswer answer = list.get(i);
					if (answer != null) {
						if (i == (list.size() - 1)) {
							answer.setPercent((100-otherPercent));
							otherPercent = 0;
						} else {
							double person = new Double(answer.getPersonRange());
							double total = new Double(answer.getTotalPerson());
							double percent = person/total*100D;
							String newPercent = formatter.format(percent);
							answer.setPercent(Integer.parseInt(newPercent));
							otherPercent += Integer.parseInt(newPercent);
						}
					}

				}

			}

		}
		return questionList;
	}
}
