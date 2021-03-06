package main.model

data class PaperInfo(
        var id: String = "",
        var title: String,
        var authors: List<String>,
        var year: String = "",
        var publisher: String,
        var institutes: List<String>,
        var type: String = "",
        var keywords: List<String>,
        var link: String,
        var abstract: String,
        var downloadCount: Int,
        var citedCount: Int,
        var refList: List<RefInfo> = ArrayList()
)