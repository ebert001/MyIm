package com.im.commons.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.im.commons.db.dao.CorporationDao;
import com.im.commons.db.entity.MCorporation;
import com.im.commons.util.ImConstants;
import com.im.spring.service.AbstractService;

@Service
public class CorporationService extends AbstractService<MCorporation> {
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
