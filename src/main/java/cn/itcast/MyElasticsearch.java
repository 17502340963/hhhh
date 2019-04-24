package cn.itcast;

import cn.itcast.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import javax.swing.text.Document;
import java.util.List;

public interface MyElasticsearch extends ElasticsearchRepository<Goods,Long>{
    List<Goods> findByPriceBetween(Double from, Double to);
}
