package com.zhqz.faces.utils.faceUtil;

import com.zhqz.faces.data.model.SearchResult;

import java.util.List;

public interface SearchResultListener {
    public void onSearchResult(String errorMessage, List<SearchResult> result);
}
