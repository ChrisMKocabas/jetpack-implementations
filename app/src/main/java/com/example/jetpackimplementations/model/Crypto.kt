package com.example.jetpackimplementations.model

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

@Serializable
data class CryptoResponse (
    val status: Status,
    val data: List<Crypto>
)

@Serializable
data class Crypto (
    val id: Long,
    val name: String,
    val symbol: String,
    val slug: String,

    @SerialName("num_market_pairs")
    val numMarketPairs: Long,

    @SerialName("date_added")
    val dateAdded: String,

    val tags: List<String>,

    @SerialName("max_supply")
    val maxSupply: Double? = null,

    @SerialName("circulating_supply")
    val circulatingSupply: Double,

    @SerialName("total_supply")
    val totalSupply: Double,

    @SerialName("infinite_supply")
    val infiniteSupply: Boolean,

    val platform: Platform? = null,

    @SerialName("cmc_rank")
    val cmcRank: Long,

    @SerialName("self_reported_circulating_supply")
    val selfReportedCirculatingSupply: Double? = null,

    @SerialName("self_reported_market_cap")
    val selfReportedMarketCap: Double? = null,

    @SerialName("tvl_ratio")
    val tvlRatio: Double? = null,

    @SerialName("last_updated")
    val lastUpdated: String,

    val quote: Quote
) {
    companion object {
        val placeholder = Crypto(
            id = -1,  // Use a unique negative value for the ID
            name = "Loading cryptos...",
            symbol = "LCR",
            slug = "loading-cryptos",
            numMarketPairs = 0,
            dateAdded = "2023-08-18",
            tags = listOf("loading", "placeholder"),
            maxSupply = 1000000.0,
            circulatingSupply = 100000.0,
            totalSupply = 1000000.0,
            infiniteSupply = false,
            platform = Platform(
                id = 1,
                name = "Sample Platform",
                symbol = "SPL",
                slug = "sample-platform",
                tokenAddress = "0x1234567890abcdef"
            ),
            cmcRank = 9999,
            selfReportedCirculatingSupply = 50000.0,
            selfReportedMarketCap = 100000.0,
            tvlRatio = 0.5,
            lastUpdated = "2023-08-18T12:00:00Z",
            quote = Quote(
                usd = Usd(
                    price = 0.0,
                    volume24H = 0.0,
                    volumeChange24H = 0.0,
                    percentChange1H = 0.0,
                    percentChange24H = 0.0,
                    percentChange7D = 0.0,
                    percentChange30D = 0.0,
                    percentChange60D = 0.0,
                    percentChange90D = 0.0,
                    marketCap = 0.0,
                    marketCapDominance = 0.0,
                    fullyDilutedMarketCap = 0.0,
                    tvl = 50000.0,
                    lastUpdated = "2023-08-18T12:00:00Z"
                )
            )
        )
    }
}

@Serializable
data class Platform (
    val id: Long,
    val name: String,
    val symbol: String,
    val slug: String,

    @SerialName("token_address")
    val tokenAddress: String
)

@Serializable
data class Quote (
    @SerialName("USD")
    val usd: Usd
)

@Serializable
data class Usd (
    val price: Double,

    @SerialName("volume_24h")
    val volume24H: Double,

    @SerialName("volume_change_24h")
    val volumeChange24H: Double,

    @SerialName("percent_change_1h")
    val percentChange1H: Double,

    @SerialName("percent_change_24h")
    val percentChange24H: Double,

    @SerialName("percent_change_7d")
    val percentChange7D: Double,

    @SerialName("percent_change_30d")
    val percentChange30D: Double,

    @SerialName("percent_change_60d")
    val percentChange60D: Double,

    @SerialName("percent_change_90d")
    val percentChange90D: Double,

    @SerialName("market_cap")
    val marketCap: Double,

    @SerialName("market_cap_dominance")
    val marketCapDominance: Double,

    @SerialName("fully_diluted_market_cap")
    val fullyDilutedMarketCap: Double,

    val tvl: Double? = null,

    @SerialName("last_updated")
    val lastUpdated: String
)

@Serializable
data class Status (
    val timestamp: String,

    @SerialName("error_code")
    val errorCode: Long,

    @SerialName("error_message")
    val errorMessage: JsonElement? = null,

    val elapsed: Long,

    @SerialName("credit_count")
    val creditCount: Long,

    val notice: JsonElement? = null,

    @SerialName("total_count")
    val totalCount: Long
)

