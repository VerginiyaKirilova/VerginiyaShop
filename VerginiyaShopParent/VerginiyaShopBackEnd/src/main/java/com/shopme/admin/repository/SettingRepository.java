package com.shopme.admin.repository;

import com.shopme.common.entity.setting.Setting;
import com.shopme.common.entity.setting.SettingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SettingRepository extends CrudRepository<Setting, String>, JpaRepository<Setting, String> {
    public List<Setting> findByCategory(SettingCategory category);
}
