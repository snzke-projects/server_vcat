package com.vcat.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by: dong4j.
 * Date: 2016-12-04.
 * Time: 13:54.
 * Description:
 */
public class ListSort {
}


class NewsManager {
    /**
     * @param args
     */
    public static void main(String[] args) {
        List newss =getNewsList();
        for(int i=0;i<newss.size();i++)
        {
            News news=(News)newss.get(i);
            System.out.println("id:"+news.getId());
            System.out.println("title:"+news.getTitle());
            System.out.println("hits:"+news.getHits());
        }
    }
    public static List getNewsList()
    {
        List list=new ArrayList();
        News news1=new News();
        news1.setHits(1);
        news1.setId(1);
        news1.setTitle("test1");
        list.add(news1);
        News news2=new News();
        news2.setHits(7);
        news2.setId(2);
        news2.setTitle("test2");
        list.add(news2);
        News news3=new News();
        news3.setHits(3);
        news3.setId(3);
        news3.setTitle("test3");
        list.add(news3);
        News news4=new News();
        news4.setHits(5);
        news4.setId(4);
        news4.setTitle("test4");
        list.add(news4);
        // 按点击数倒序
        Collections.sort(list, new Comparator<News>() {
            public int compare(News arg0, News arg1) {
                int hits0 = arg0.getHits();
                int hits1 = arg1.getHits();
                if (hits1 > hits0) {
                    return 1;
                } else if (hits1 == hits0) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        return list;
    }
}

class News{
    private int hits;
    private int id;
    private String title;

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}