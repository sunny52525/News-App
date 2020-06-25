package com.shaun.news

class newsData(
    var websiteName: String,
    var author: String,
    var title: String,
    var description: String,
    var urlToArticle: String,
    var urlToImage: String,
    var datePublished: String
) {
    override fun toString(): String {
        return """
            
            [Website = $websiteName 
            Author = $author
            Title = $title
            description = $description
            urlToarticle = $urlToArticle
            urltoImage = $urlToImage
            Publised = $datePublished]
        """.trimIndent()
    }
}