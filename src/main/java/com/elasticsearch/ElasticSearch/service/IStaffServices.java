package com.elasticsearch.ElasticSearch.service;

import com.elasticsearch.ElasticSearch.dto.request.UpdateStaffRequest;
import com.elasticsearch.ElasticSearch.entity.Staff;

import java.io.IOException;
import java.util.List;

public interface IStaffServices {
    String indexStaff(Staff staff) throws IOException;

    Staff getStaffById(String staffId) throws IOException;

    String updateStaff(String staffId, UpdateStaffRequest request) throws IOException;

    List<Staff> searchStaffsByFullName(String searchText) throws IOException;

    List<Staff> getAllStaffs() throws IOException;

    String deleteStaff(String staffId) throws IOException;
}
