package com.example.associativesearch.service;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.JSON;
import com.example.associativesearch.config.Config;
import com.example.associativesearch.model.Concept;
import com.example.associativesearch.model.DescriptorRecord;
import com.example.associativesearch.model.Key;
import com.example.associativesearch.model.Term;
import com.example.associativesearch.util.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


//@Component
public class Task implements ApplicationRunner {
    @Autowired
    Utils utils;
    @Autowired
    Config config;
    @Autowired
    EsService esService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String index = "descriptor-keys";
        Long totalKey = esService.getTotal(index);
        System.out.println("关键词量:"+totalKey);
        ArrayList<String> keys = new ArrayList<>();
        for (long i = config.getKeyIndex(); i < totalKey; i = i + 1000) {
            System.out.println("获取关键词："+i);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            searchSourceBuilder.size(1000);
            searchSourceBuilder.from((int) i);
            List<DescriptorRecord> articles = esService.search(index, searchSourceBuilder, new TypeReference<DescriptorRecord>() {
                    },
                    null, Pageable.ofSize(3)).getContent();

            for (DescriptorRecord article : articles) {
                keys.add(article.getDescriptorName());
                for (Concept concept : article.getConceptList()) {
                    keys.add(concept.getConceptName());
                    for (Term term : concept.getTermList()) {
                        keys.add(term.getString());
                    }
                }
            }

        }


        //遍历关键字爬取网站
        String searchUrl = "https://www.webmd.com/search/search_results/default.aspx?query=";
        for (int i=0;i<keys.size();i++) {
            String key = keys.get(i);
            if (StringUtils.isBlank(key)){
                continue;
            }
            Long total = utils.getTotal(searchUrl, key);
            System.out.println("key序号："+i);
            System.out.println(key+" 相关总数：" + total);
            for (int j = 1; j <= total; j++) {
                utils.getArtlcles(searchUrl, key, (long) j);
            }

        }

    }


    public void test1() {
        Document document = XmlUtil.readXML("D:\\Workspace\\java-project\\meshRelationalQuery\\associative-search\\src\\main\\resources\\covid-19.xml");
        Element rootElement = XmlUtil.getRootElement(document);
        List<Element> descriptorRecords = XmlUtil.getElements(rootElement, "DescriptorRecord");
        ArrayList<DescriptorRecord> descriptorList = new ArrayList<>();
        ArrayList<Key> keyList = new ArrayList<>();

        for (Element descriptorRecord : descriptorRecords) {
            DescriptorRecord descriptorRecordModel = new DescriptorRecord();

            Element descriptorName = XmlUtil.getElement(descriptorRecord, "DescriptorName");
            Element name = XmlUtil.getElement(descriptorName, "String");
            String textContent = name.getFirstChild().getTextContent();
            descriptorRecordModel.setDescriptorName(textContent);
            keyList.add(Key.builder().id(descriptorRecordModel.getId()).content(descriptorRecordModel.getDescriptorName()).build());


            Element conceptList = XmlUtil.getElement(descriptorRecord, "ConceptList");
            List<Element> concepts = XmlUtil.getElements(conceptList, "Concept");

            List<Concept> conceptListModel = new ArrayList<>();
            for (Element concept : concepts) {
                Concept conceptModel = new Concept();
                List<Term> terms = new ArrayList<>();
                Element conceptName = XmlUtil.getElement(concept, "ConceptName");
                Element scopeNote = XmlUtil.getElement(concept, "ScopeNote");
                if (scopeNote!=null){
                    String des = scopeNote.getTextContent();
                    conceptModel.setScopeNote(des);
                }
                String conceptName1 = XmlUtil.getElement(conceptName, "String").getFirstChild().getTextContent();
                conceptModel.setConceptName(conceptName1);
                conceptListModel.add(conceptModel);
                keyList.add(Key.builder().id(conceptModel.getId()).content(conceptModel.getConceptName()).build());


                Element termList = XmlUtil.getElement(concept, "TermList");
                List<Element> termEs = XmlUtil.getElements(termList, "Term");
                for (Element term : termEs) {
                    Term termModel = new Term();
                    String termName = XmlUtil.getElement(term, "String").getFirstChild().getTextContent();
                    termModel.setString(termName);
                    keyList.add(Key.builder().id(termModel.getId()).content(termModel.getString()).build());
                    terms.add(termModel);
                }
                conceptModel.setTermList(terms);

            }
            descriptorRecordModel.setConceptList(conceptListModel);
            descriptorList.add(descriptorRecordModel);
            //System.out.println(descriptorRecordModel);
        }
        System.out.println(JSON.toJSONString(descriptorList));
        //esService.addToEs(descriptorList, "descriptor-keys");
        //esService.addSourceToEs(keyList, "keys");
    }
}
