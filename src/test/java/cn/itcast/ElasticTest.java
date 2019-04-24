package cn.itcast;

import cn.itcast.pojo.Goods;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.annotation.Native;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticTest {
    @Autowired
    private ElasticsearchTemplate template;
    @Autowired
    private MyElasticsearch myElasticsearch;
    @Test
    public void test1(){

        Goods goods = new Goods(1L, "小米手机", "手机", "小米", 2444.00, "asdjlksajd");
        myElasticsearch.save(goods);
    }
    @Test
    public void test2(){
       /* Optional<Goods> byId = myElasticsearch.findById(1l);
        System.out.println(byId);*/
   /*     Iterable<Goods> all = myElasticsearch.findAll();
        all.forEach(System.out::println);*/
    /*    List<Goods> between = myElasticsearch.findByPriceBetween(1000.0, 4000.0);
        between.forEach(System.out::println);*/

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withQuery(QueryBuilders.matchQuery("title","小米手机"));
        searchQueryBuilder.withPageable(PageRequest.of(0,2, Sort.by(Sort.Direction.ASC,"price")));
        searchQueryBuilder.addAggregation(AggregationBuilders.terms("brands").field("brand"));
        AggregatedPage<Goods> result = template.queryForPage(searchQueryBuilder.build(), Goods.class);

    }
}
