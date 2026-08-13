package com.foxconn.iad.module.esd.service;

import com.foxconn.iad.module.esd.controller.admin.laundry.vo.LaundryRespVO;

import java.util.List;

/**
 * 清洗業務服務。
 *
 * <p>清洗管理承接回收送洗的資產：送洗登記將待送洗推進為清洗中，
 * 完成清洗將清洗中資產恢復為庫存並累加清洗次數。</p>
 */
public interface LaundryService {

    /** 查詢待送洗與清洗中的進行中清洗列表。 */
    List<LaundryRespVO> activePage(String siteCode);

    /** 送洗登記：待送洗 -> 清洗中。 */
    void start(Long id, String siteCode);

    /** 完成清洗：清洗中 -> 庫存，清洗次數 +1。 */
    void complete(Long id, String siteCode);
}
