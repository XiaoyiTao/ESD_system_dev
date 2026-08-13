-- ESD System v0.3.0
-- 六態狀態機、回收記錄簡化、發放快照擴展。
-- 依《問題補充.txt》補充需求 #6（六態）、#12（回收去掉回收狀態）、#3（發放補人員快照）。
USE platform_esd;

-- 1. 資產狀態改為六態：新增「待送洗(25)」
ALTER TABLE esd_asset DROP CHECK ck_asset_status;
ALTER TABLE esd_asset
  ADD CONSTRAINT ck_asset_status CHECK (lifecycle_status IN (10, 20, 25, 30, 40, 50));

-- 2. 回收記錄移除「回收狀態」欄位（正常/損壞/髒污），並補資產類型快照
ALTER TABLE esd_return_record DROP COLUMN return_condition;
ALTER TABLE esd_return_record
  ADD COLUMN asset_type TINYINT NOT NULL DEFAULT 1 COMMENT '資產類型快照：1 靜電衣，2 靜電鞋' AFTER asset_code;

-- 3. 發放記錄補人員快照欄位（責任主管/課別/樓層），供發放後從人員檔快照寫入
ALTER TABLE esd_issue_record
  ADD COLUMN supervisor_name VARCHAR(100) NULL COMMENT '責任主管姓名快照' AFTER employee_name,
  ADD COLUMN dept_name VARCHAR(100) NULL COMMENT '課別名稱快照' AFTER supervisor_name,
  ADD COLUMN floor_code VARCHAR(32) NULL COMMENT '樓層代碼快照' AFTER dept_name;

-- 4. 清洗記錄送洗欄位改為可空：回收送洗時僅建「待送洗」記錄，送洗登記後才寫入送洗時間與操作人
ALTER TABLE esd_laundry_record MODIFY send_time DATETIME NULL;
ALTER TABLE esd_laundry_record MODIFY send_operator_user_id BIGINT NULL;
