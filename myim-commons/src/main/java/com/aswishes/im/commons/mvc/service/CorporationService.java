package com.aswishes.im.commons.mvc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aswishes.im.commons.mvc.dao.CorporationDao;
import com.aswishes.im.commons.mvc.entity.MCorporation;
import com.aswishes.im.commons.util.ImConstants;
import com.aswishes.spring.service.AbstractService;

@Service
public class CorporationService extends AbstractService {
	@Autowired
	private CorporationDao corporationDao;
	@Override
	public void setDao() {
		this.dao = corporationDao;
	}

	@Transactional
	public MCorporation getOfficial() {
		MCorporation corp = corporationDao.get(1L);
		if (corp == null) {
			synchronized (this) {
				corporationDao.save(ImConstants.OFFICIAL_NAME);
				corp = corporationDao.get(1L);
			}
		}
		return corp;
	}
}
