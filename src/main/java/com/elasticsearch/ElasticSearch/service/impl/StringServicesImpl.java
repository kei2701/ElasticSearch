package com.elasticsearch.ElasticSearch.service.impl;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.elasticsearch.ElasticSearch.constant.Index;
import com.elasticsearch.ElasticSearch.entity.Province;
import com.elasticsearch.ElasticSearch.service.IStringServices;
import com.elasticsearch.ElasticSearch.util.ElasticsearchClientUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Year;
import java.util.regex.Pattern;

import static com.elasticsearch.ElasticSearch.constant.PhoneNumberRegex.ELEVEN_DIGIT_REGEX;
import static com.elasticsearch.ElasticSearch.constant.PhoneNumberRegex.TEN_DIGIT_REGEX;

@Service
public class StringServicesImpl implements IStringServices {
    private final RestTemplate restTemplate;

    public StringServicesImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String validateString(String stringNumber) throws IOException {
        if (isValidIdentityNumber(stringNumber)) {
            return "It is identity number";
        } else if (isValidPhoneNumber(stringNumber)) {
            return "It is phone number";
        } else if(isValidTaxCode(stringNumber)) {
            return "It is tax code";
        } else {
            return "String number is not valid";
        }
    }

    private boolean isValidIdentityNumber(String stringNumber) throws IOException {
        if (stringNumber.length() != 12) {
            return false;
        }

        String provinceId = stringNumber.substring(0, 3);
        String genderCode = stringNumber.substring(3, 4);
        String birthYear = stringNumber.substring(4, 6);

        // Kiểm tra mã tỉnh
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(Index.PROVINCES)
                .query(q -> q.term(s -> s.field("provinceId").value(provinceId)))
                .build();
        SearchResponse<Province> searchResponse = ElasticsearchClientUtil.createClient()
                .search(searchRequest, Province.class);
        if (searchResponse.hits().hits().isEmpty()) {
            return false;
        }

        // Kiểm tra mã giới tính
        int yearNow = Year.now().getValue() - 2014;
        int birthCentury = Integer.parseInt(birthYear) <= yearNow &&  Integer.parseInt(birthYear) > 0 ? 21 : 20;
        if ((birthCentury == 20 && !"0".equals(genderCode) && !"1".equals(genderCode)) ||
                (birthCentury == 21 && !"2".equals(genderCode) && !"3".equals(genderCode))) {
            return false;
        }
        return true;
    }

    private boolean isValidPhoneNumber(String stringNumber) {
        return Pattern.matches(ELEVEN_DIGIT_REGEX, stringNumber) || Pattern.matches(TEN_DIGIT_REGEX, stringNumber);
    }

    private boolean isValidTaxCode(String stringNumber) {
        String url = "https://masothue.com/Search/?q=" + stringNumber + "&type=auto&force-search=1";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        return response.getBody() != null && response.getStatusCode().value() == 200 &&
                response.getBody().contains("<td><i class='fa fa-map-marker'></i> Địa chỉ</td>") &&
                response.getBody().contains("<td><i class='fa fa-user'></i> Người đại diện</td>") &&
                response.getBody().contains("<td><i class='fa fa-calendar'></i> Ngày hoạt động</td>");
    }
}
