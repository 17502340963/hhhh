package cn.itcast;

import cn.itcast.pojo.Item;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.text.Highlighter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@SuppressWarnings("all")
@RunWith(SpringRunner.class)
@SpringBootTest
public class Highlewcli {
private RestHighLevelClient client;
    // Json工具
    private Gson gson = new Gson();
    @Before
    public void init(){
        //初始化highlevel客户端
        client = new RestHighLevelClient(RestClient.builder(
                new HttpHost("192.168.11.128",9201),
                new HttpHost("192.168.11.128",9202),
                new HttpHost("192.168.11.128",9203)));
    }
    @Test
    public void test1() throws IOException {
        Item item = new Item(1L, "小米电脑", "电脑", "小米", 2600.00, "http://asdsadsad");
        //把要添加的信息转换成json
        String sourct = gson.toJson(item);
        //创建新增请求调用source把要添加的信息传进去
        IndexRequest request = new IndexRequest("item", "docs", item.getId().toString()).source(sourct, XContentType.JSON);
        //执行请求传入亲求对象
        IndexResponse index = client.index(request, RequestOptions.DEFAULT);
    }
    @Test
    public void test2() throws IOException {
        //创建查看索引请求
        GetRequest request = new GetRequest("item","docs","1");
        //执行亲求
        request.fetchSourceContext(new FetchSourceContext(true,new String[]{"title"},null));
        GetResponse fields = client.get(request, RequestOptions.DEFAULT);
        //解析亲求
        String source = fields.getSourceAsString();
        System.out.println(source);
        System.out.println("大家好这里是合并完成后的代码");
    }
    @Test
    public void test3() throws IOException {
        //创建删除对象
        DeleteRequest deleteRequest = new DeleteRequest("item","docs","1");
        //执行删除
        DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(response);
    }
    @Test
    public void test4() throws IOException {
        // 准备文档数据：
        List<Item> list = new ArrayList<>();
        list.add(new Item(1L, "小米手机7", "手机", "小米", 3299.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(2L, "坚果手机R1", "手机", "锤子", 3699.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(3L, "华为META10", "手机", "华为", 4499.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(4L, "小米Mix2S", "手机", "小米", 4299.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(5L, "荣耀V10", "手机", "华为", 2799.00, "http://image.leyou.com/13123.jpg"));
        //创建批量新增请求
        BulkRequest bulkRequest = new BulkRequest();
        for (Item item : list) {
            //构建单个IndexRequest，并add到BulkRequest中去
            bulkRequest.add(new IndexRequest("item","docs",item.getId().toString()).source(gson.toJson(item),XContentType.JSON));
        }
        client.bulk(bulkRequest,RequestOptions.DEFAULT);
    }

    @Test
    public void test5() throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //添加查询条件
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        //把构建起添加到请求对象中
        basicSearch(sourceBuilder);
    }
    @Test
    public void test6() throws IOException {
        //创建搜索资源构建工具
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //添加查询条件
        sourceBuilder.query(QueryBuilders.matchQuery("title","小米手机").operator(Operator.AND));
        basicSearch( sourceBuilder);
    }
    @Test
    public void test7() throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.rangeQuery("price").gte(1000).lte(4000));
        basicSearch(sourceBuilder);
    }
    @Test
    public void test8() throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        sourceBuilder.fetchSource(new String[]{"id","title"},null);
        basicSearch(sourceBuilder);
    }
    @Test
    public void test9() throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        sourceBuilder.sort("price", SortOrder.ASC);
        sourceBuilder.sort("id",SortOrder.DESC);
        basicSearch(sourceBuilder);
    }
    @Test
    public void test10() throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        sourceBuilder.sort("price",SortOrder.ASC);
        int page = 2;
        int size = 3;
        sourceBuilder.from((page-1)*size);
        sourceBuilder.size(size);
        basicSearch(sourceBuilder);
    }
    @Test
    public void test11() throws IOException {
        SearchRequest request = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        sourceBuilder.sort("price",SortOrder.ASC);
        sourceBuilder.size(0);
        sourceBuilder.aggregation(AggregationBuilders.terms("brandagg").field("brand"));
        request.source(sourceBuilder);
        SearchResponse response = client.search(request,RequestOptions.DEFAULT);
        Aggregations aggregations = response.getAggregations();
        Terms terms = aggregations.get("brandagg");
        for (Terms.Bucket bucket : terms.getBuckets()) {
            System.out.println(bucket.getKeyAsString());
            System.out.println(bucket.getDocCount());
        }


    }

    private void basicSearch( SearchSourceBuilder sourceBuilder) throws IOException {
        //创建搜索请求
        SearchRequest request = new SearchRequest();
        //把构建起添加到请求对象中
        request.source(sourceBuilder);
        //发送请求返回数据
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        //获取hits对象
        SearchHits hits = search.getHits();
        //获取hits数组
        SearchHit[] hits1 = hits.getHits();
        //遍历数组获取source中的数据
        for (SearchHit hit : hits1) {
            String json = hit.getSourceAsString();
            Item item = gson.fromJson(json, Item.class);
            System.out.println(item);
        }
    }
    @Test
    public void test12() throws IOException {
        SearchRequest request = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("title","小米手机"));
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<em style='color:red'>");
        highlightBuilder.postTags("</em>");
        sourceBuilder.highlighter(highlightBuilder);
        request.source(sourceBuilder);
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        SearchHit[] hitsHits = hits.getHits();
        for (SearchHit hit : hitsHits) {
            Map<String, HighlightField> fields = hit.getHighlightFields();
            HighlightField title = fields.get("title");
            String join = StringUtils.join(title.getFragments());
            Item item = gson.fromJson(hit.getSourceAsString(),Item.class);
            item.setTitle(join);
            System.out.println(item.getTitle());
        }


    }
    @After
    public void close() throws IOException {
        client.close();
    }
}
