package com.github.iappapp.panda.utils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.AttributeProviderContext;
import org.commonmark.renderer.html.AttributeProviderFactory;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 */
public class MarkdownToHtmlUtils {

    /**
     * markdown格式转换成HTML格式
     * @param markdown
     * @return
     */
    public static String markdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    /**
     * 增加扩展[标题锚点，表格生成]
     * Markdown转换成HTML
     * @param markdown
     * @return
     */
    public static String markdownToHtmlExtensions(String markdown) {
        //h标题生成id
        Set<Extension> headingAnchorExtensions = Collections.singleton(HeadingAnchorExtension.create());
        //转换table的HTML
//        List<Extension> tableExtension = Arrays.asList(TablesExtension.create());
        List<Extension> tableExtension = Collections.singletonList(TablesExtension.create());
        Parser parser = Parser.builder()
                .extensions(tableExtension)
                .build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(headingAnchorExtensions)
                .extensions(tableExtension)
                .attributeProviderFactory(new AttributeProviderFactory() {
                    public AttributeProvider create(AttributeProviderContext context) {
                        return new CustomAttributeProvider();
                    }
                })
                .build();
        return renderer.render(document);
    }

    /**
     * 处理标签的属性
     */
    static class CustomAttributeProvider implements AttributeProvider {
        @Override
        public void setAttributes(Node node, String tagName, Map<String, String> attributes) {
            //改变a标签的target属性为_blank
            if (node instanceof Link) {
                attributes.put("target", "_blank");
            }
            if (node instanceof TableBlock) {
                attributes.put("class", "ui celled table");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String src = "# 一、标题\n ## 1.测试\n ```这是一段测试的文本```";
        //
        src = "## 系统设计\n" +
                "\n" +
                "### nginx后端服务转发设计\n" +
                "\n" +
                "\n" +
                "| 项目名称 | 上下文 | 端口号  | 备注 |\n" +
                "| --- | --- | --- | --- |\n" +
                "| rightplatform | rightplatform | 8868 |  |\n" +
                "| g5| g5 | 8869 | |\n" +
                "| smartzone | smartzone| 8870 | | \n" +
                "| interface5g| interface5g | 8871 | \n" +
                "| intellplat | intellplat | 8872 | |\n" +
                "| groupActivity | groupActivity| 8873 | |\n" +
                "| funbill | funbill | 8874 | |\n" +
                "| couponActivity | couponActivity | 8875 | |\n" +
                "\n" +
                "## nginx 前端服务转发设计\n" +
                "\n" +
                "| 项目名称 | 上下文  | 端口号 | 备注 |\n" +
                "| --- | --- | --- | --- |\n" +
                "| g5-android | g5Zone  | 80  |  |\n" +
                "| g5-ios  | g5ZoneIos | 80  |  |\n" +
                "| g5-wap | g5Wap | 80 |  |\n" +
                "| g5-wap-js | g5Floor | 80 | |\n" +
                "| rightplatform | right-web| 80 | |\n" +
                "| interRoming | interRoming | 80 | |\n" +
                "| interest-bill | inter-bill | 80 | |\n" +
                "| group-welfare| group-welfare | 80 | |\n" +
                "| go-broadband | go-broadband | 80 | |\n" +
                "| go-activity | go-activity | 80 | |\n" +
                "\n";
        
        String result = markdownToHtmlExtensions(src);
        System.out.println(result);
    }

}
