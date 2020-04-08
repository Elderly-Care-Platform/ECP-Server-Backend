package com.beautifulyears.repository.custom;

import org.springframework.data.domain.Pageable;
import java.util.List;
import com.beautifulyears.domain.JustDailServices;
import com.beautifulyears.rest.response.PageImpl;

public interface JustDialServiceRepositoryCustom {

	public PageImpl<JustDailServices> getServiceProvidersByFilterCriteria(String name, String catId, String subCatId,Pageable page);

	public long getServiceProvidersByFilterCriteriaCount(String name, String catId, String subCatId);

	// public PageImpl<JustDailServices> findAllServices(Pageable pageable);

	// public JustDailServices findByServiceId(String docId);
}