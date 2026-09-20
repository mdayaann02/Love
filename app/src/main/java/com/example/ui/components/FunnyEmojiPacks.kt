package com.example.ui.components

data class EmojiPack(
    val id: String,
    val title: String,
    val iconEmoji: String,
    val badge: String,
    val emojis: List<String>
)

object FunnyEmojiPacks {
    val packs = listOf(
        EmojiPack(
            id = "derp",
            title = "Derp & Goofy",
            iconEmoji = "🤪",
            badge = "FUNNY",
            emojis = listOf(
                "🤪", "🫠", "🤡", "🗿", "🦹‍♂️", "🥴", "🤤", "🤓",
                "🥸", "🤠", "👻", "👽", "💩", "👹", "🫥", "🥳",
                "😈", "🤖", "👾", "🫨", "😵‍💫", "🤯", "🥶", "🥵"
            )
        ),
        EmojiPack(
            id = "snap",
            title = "Snap Classics",
            iconEmoji = "🔥",
            badge = "STREAK",
            emojis = listOf(
                "💛", "🔥", "⚡", "👀", "💯", "📸", "🕶️", "🤫",
                "🔒", "💬", "🪄", "✨", "🍕", "🏆", "💎", "👑",
                "⏳", "🔑", "🎯", "🎉", "🍿", "🍔", "🍟", "🧋"
            )
        ),
        EmojiPack(
            id = "drama",
            title = "Drama & Sassy",
            iconEmoji = "💅",
            badge = "SPICY",
            emojis = listOf(
                "💅", "💀", "🍿", "🐸☕", "🙄", "🤦‍♀️", "🫖", "🎭",
                "💃", "🤐", "🚨", "💣", "😒", "🤷‍♂️", "👀", "💅🏼",
                "🥀", "🤌", "🫦", "💅🏽", "💅🏾", "💅🏿", "👁️👄👁️", "🫣"
            )
        ),
        EmojiPack(
            id = "wild",
            title = "Wild Animals",
            iconEmoji = "🐒",
            badge = "CHAOS",
            emojis = listOf(
                "🐒", "🐈‍⬛", "🦝", "🦆", "🦥", "🦄", "🦖", "🐙",
                "🦩", "🦧", "🦨", "🦦", "🦇", "🦔", "🦭", "🐸",
                "🦈", "🐝", "🦀", "🐡", "🦜", "🦚", "🐺", "🦊"
            )
        ),
        EmojiPack(
            id = "meme",
            title = "Meme Chaos",
            iconEmoji = "🚀",
            badge = "MEME",
            emojis = listOf(
                "🚀", "💥", "🥑", "🌮", "🧇", "🍩", "🧃", "🛸",
                "🪐", "🧩", "🧻", "🚽", "🌭", "🧠", "🤯", "🌈",
                "🛹", "🎸", "🧨", "🔮", "💸", "🧬", "🧪", "🪩"
            )
        )
    )

    val quickReactions = listOf("💛", "🔥", "💀", "🤪", "👀", "🗿", "💯", "💅")
}
