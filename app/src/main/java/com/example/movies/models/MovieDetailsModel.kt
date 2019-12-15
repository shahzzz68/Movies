package com.example.movies.models

data class MovieDetailsModel(
    var adult: Boolean,
    var backdropPath: String,
    var belongsToCollection: BelongsToCollection,
    var budget: Int,
    var genres: List<Genre>,
    var homepage: Any?,
    var id: Int,
    var imdbId: String,
    var originalLanguage: String,
    var originalTitle: String,
    var overview: String,
    var popularity: Double,
    var poster_path: String,
    var production_companies: List<ProductionCompany>?,
    var production_countries: List<ProductionCountry>?,
    var release_date: String?,
    var revenue: Int,
    var runtime: Int,
    var spokenLanguages: List<SpokenLanguage>,
    var status: String,
    var tagline: String,
    var title: String,
    var video: Boolean,
    var vote_average: Double,
    var vote_count: Int
) {
    data class BelongsToCollection(
        var backdropPath: String,
        var id: Int,
        var name: String,
        var posterPath: String
    )

    data class Genre(
        var id: Int,
        var name: String
    )

    data class ProductionCompany(
        var id: Int,
        var logoPath: String,
        var name: String,
        var origin_country: String
    )

    data class ProductionCountry(
        var iso31661: String,
        var name: String
    )

    data class SpokenLanguage(
        var iso6391: String,
        var name: String
    )
}