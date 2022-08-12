package de.solugo.model

import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.isEmpty
import io.ktor.websocket.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ContextTest {

    val context = Context()
    val roomId = "2c8ab43e-5daa-4b3f-9aec-dd573d228521"
    val player1Id = "c02b43bf-74d2-4bdf-a9f1-23ec8413f485"
    val player1Channel = Channel<Frame>(Channel.UNLIMITED)
    val player2Id = "a6b716b5-8545-45df-9377-ed682918747f"
    val player2Channel = Channel<Frame>(Channel.UNLIMITED)
    val player3Id = "4642edad-dfa5-4211-8b76-bac9572d329c"
    val player3Channel = Channel<Frame>(Channel.UNLIMITED)

    @Test
    @Order(1)
    fun joinRoomPlayer1() {
        runBlocking {
            context.joinRoom(roomId, player1Id, player1Channel)

            assertThat(player1Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("playerJoinedRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player1Id))
                },
                buildJsonObject {
                    put("type", JsonPrimitive("playerInfoChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player1Id))
                    put("name", JsonNull)
                },
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(player1Id, JsonNull)
                    })
                },
                buildJsonObject {
                    put("type", JsonPrimitive("roomInfoChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("name", JsonNull)
                    put("options", buildJsonObject {
                        put("½", JsonPrimitive(0.5))
                        put("1", JsonPrimitive(1.0))
                        put("2", JsonPrimitive(2.0))
                        put("3", JsonPrimitive(3.0))
                        put("5", JsonPrimitive(5.0))
                        put("8", JsonPrimitive(8.0))
                        put("13", JsonPrimitive(13.0))
                        put("21", JsonPrimitive(21.0))
                        put("♾", JsonNull)
                        put("☕", JsonNull)
                        put("⚡", JsonNull)
                    })
                },
            )
        }
    }

    @Test
    @Order(2)
    fun updatePlayer1Info() {
        runBlocking {
            context.updatePlayerInfo(roomId, player1Id, "Player 1")

            assertThat(player1Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("playerInfoChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player1Id))
                    put("name", JsonPrimitive("Player 1"))
                },
            )
        }
    }

    @Test
    @Order(3)
    fun updatePlayer1Selection() {
        runBlocking {
            context.updatePlayerSelection(roomId, player1Id, "½")

            assertThat(player1Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(player1Id, JsonPrimitive("½"))
                    })
                },
            )
        }
    }

    @Test
    @Order(4)
    fun updateRoomWrongInfo() {
        runBlocking {
            context.updateRoomInfo(roomId, name = "Wrong Room")

            assertThat(player1Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomInfoChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("name", JsonPrimitive("Wrong Room"))
                    put("options", buildJsonObject {
                        put("½", JsonPrimitive(0.5))
                        put("1", JsonPrimitive(1.0))
                        put("2", JsonPrimitive(2.0))
                        put("3", JsonPrimitive(3.0))
                        put("5", JsonPrimitive(5.0))
                        put("8", JsonPrimitive(8.0))
                        put("13", JsonPrimitive(13.0))
                        put("21", JsonPrimitive(21.0))
                        put("♾", JsonNull)
                        put("☕", JsonNull)
                        put("⚡", JsonNull)
                    })
                },
            )
        }
    }

    @Test
    @Order(5)
    fun updateRoomRightInfo() {
        runBlocking {
            context.updateRoomInfo(roomId, name = "Right Room", options = mapOf("1" to 1.0, "2" to 2.0))

            assertThat(player1Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomInfoChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("name", JsonPrimitive("Right Room"))
                    put("options", buildJsonObject {
                        put("1", JsonPrimitive(1.0))
                        put("2", JsonPrimitive(2.0))
                    })
                },
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(player1Id, JsonNull)
                    })
                },
            )
        }
    }

    @Test
    @Order(6)
    fun updatePlayer1SelectionAfterOptionChange() {
        runBlocking {
            context.updatePlayerSelection(roomId, player1Id, selection = "2")

            assertThat(player1Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(player1Id, JsonPrimitive("2"))
                    })
                },
            )
        }
    }

    @Test
    @Order(7)
    fun joinRoomPlayer2() {
        runBlocking {
            context.joinRoom(roomId, player2Id, player2Channel)

            assertThat(player1Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("playerJoinedRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player2Id))
                },
                buildJsonObject {
                    put("type", JsonPrimitive("playerInfoChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player2Id))
                    put("name", JsonNull)
                },
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(player1Id, "2")
                        put(player2Id, JsonNull)
                    })
                },
            )

            assertThat(player2Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("playerJoinedRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player1Id))
                },
                buildJsonObject {
                    put("type", JsonPrimitive("playerJoinedRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player2Id))
                },
                buildJsonObject {
                    put("type", JsonPrimitive("playerInfoChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player1Id))
                    put("name", "Player 1")
                },
                buildJsonObject {
                    put("type", JsonPrimitive("playerInfoChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player2Id))
                    put("name", JsonNull)
                },
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(player1Id, "")
                        put(player2Id, JsonNull)
                    })
                },
                buildJsonObject {
                    put("type", JsonPrimitive("roomInfoChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("name", JsonPrimitive("Right Room"))
                    put("options", buildJsonObject {
                        put("1", JsonPrimitive(1.0))
                        put("2", JsonPrimitive(2.0))
                    })
                },
            )
        }
    }

    @Test
    @Order(8)
    fun updatePlayer2Info() {
        runBlocking {
            context.updatePlayerInfo(roomId, player2Id, "Player 2")

            assertThat(player1Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("playerInfoChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player2Id))
                    put("name", JsonPrimitive("Player 2"))
                },
            )

            assertThat(player2Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("playerInfoChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player2Id))
                    put("name", JsonPrimitive("Player 2"))
                },
            )
        }
    }

    @Test
    @Order(9)
    fun updatePlayer2Selection() {
        runBlocking {
            context.updatePlayerSelection(roomId, player2Id, "1")

            assertThat(player1Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(player1Id, "2")
                        put(player2Id, "")
                    })
                },
            )

            assertThat(player2Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(player1Id, "")
                        put(player2Id, "1")
                    })
                },
            )
        }
    }

    @Test
    @Order(10)
    fun joinRoomPlayer3() {
        runBlocking {
            context.joinRoom(roomId, player3Id, player3Channel)

            assertThat(player1Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("playerJoinedRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player3Id))
                },
                buildJsonObject {
                    put("type", JsonPrimitive("playerInfoChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player3Id))
                    put("name", JsonNull)
                },
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(player1Id, "2")
                        put(player2Id, "")
                        put(player3Id, JsonNull)
                    })
                },
            )

            assertThat(player2Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("playerJoinedRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player3Id))
                },
                buildJsonObject {
                    put("type", JsonPrimitive("playerInfoChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player3Id))
                    put("name", JsonNull)
                },
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(player1Id, "")
                        put(player2Id, "1")
                        put(player3Id, JsonNull)
                    })
                },
            )

            assertThat(player3Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("playerJoinedRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player1Id))
                },
                buildJsonObject {
                    put("type", JsonPrimitive("playerJoinedRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player2Id))
                },
                buildJsonObject {
                    put("type", JsonPrimitive("playerJoinedRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player3Id))
                },
                buildJsonObject {
                    put("type", JsonPrimitive("playerInfoChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player1Id))
                    put("name", "Player 1")
                },
                buildJsonObject {
                    put("type", JsonPrimitive("playerInfoChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player2Id))
                    put("name", "Player 2")
                },
                buildJsonObject {
                    put("type", JsonPrimitive("playerInfoChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player3Id))
                    put("name", JsonNull)
                },
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(player1Id, "")
                        put(player2Id, "")
                        put(player3Id, JsonNull)
                    })
                },
                buildJsonObject {
                    put("type", JsonPrimitive("roomInfoChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("name", JsonPrimitive("Right Room"))
                    put("options", buildJsonObject {
                        put("1", JsonPrimitive(1.0))
                        put("2", JsonPrimitive(2.0))
                    })
                },
            )
        }
    }

    @Test
    @Order(11)
    fun updatePlayer3Selection() {
        runBlocking {
            context.updatePlayerSelection(roomId, player3Id, "1")

            assertThat(player1Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(player1Id, "2")
                        put(player2Id, "")
                        put(player3Id, "")
                    })
                },
            )

            assertThat(player2Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(player1Id, "")
                        put(player2Id, "1")
                        put(player3Id, "")
                    })
                },
            )
            assertThat(player3Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(player1Id, "")
                        put(player2Id, "")
                        put(player3Id, "1")
                    })
                },
            )
        }
    }

    @Test
    @Order(12)
    fun leaveRoomPlayer1() {
        runBlocking {
            context.leaveRoom(roomId, player1Id)

            assertThat(player1Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("playerLeftRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player1Id))
                },
            )

            assertThat(player2Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("playerLeftRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player1Id))
                },
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(player2Id, "1")
                        put(player3Id, "")
                    })
                },
            )
            assertThat(player3Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("playerLeftRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player1Id))
                },
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(player2Id, "")
                        put(player3Id, "1")
                    })
                },
            )
        }
    }

    @Test
    @Order(13)
    fun revealRoom() {
        runBlocking {
            context.revealRoom(roomId, true)

            assertThat(player1Channel.purge()).isEmpty()

            assertThat(player2Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(true))
                    put("selections", buildJsonObject {
                        put(player2Id, "1")
                        put(player3Id, "1")
                    })
                },
            )
            assertThat(player3Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(true))
                    put("selections", buildJsonObject {
                        put(player2Id, "1")
                        put(player3Id, "1")
                    })
                },
            )
        }
    }

    @Test
    @Order(14)
    fun resetRoom() {
        runBlocking {
            context.resetRoom(roomId)

            assertThat(player1Channel.purge()).isEmpty()

            assertThat(player2Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(player2Id, JsonNull)
                        put(player3Id, JsonNull)
                    })
                },
            )
            assertThat(player3Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(player2Id, JsonNull)
                        put(player3Id, JsonNull)
                    })
                },
            )
        }
    }

    @Test
    @Order(15)
    fun leaveRoomPlayer2() {
        runBlocking {
            player2Channel.close()
            context.removePlayer(player2Id)

            assertThat(player1Channel.purge()).isEmpty()
            assertThat(player2Channel.purge()).isEmpty()

            assertThat(player3Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("playerLeftRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("playerId", JsonPrimitive(player2Id))
                },
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(player3Id, JsonNull)
                    })
                },
            )
        }
    }

    @BeforeEach
    fun clear() {
        player1Channel.purge()
        player2Channel.purge()
        player3Channel.purge()
    }

//            @Test
//    @Order(2)
//    fun lifecycle() {
//
//
//        runBlocking {
//
//
//
//            player2Channel.close()
//            context.removePlayer(player2Id)
//
//            assertThat(player1Channel.purge()).isEmpty()
//
//            assertThat(player2Channel.purge()).isEmpty()
//
//            assertThat(player3Channel.purge()).containsExactlyInAnyOrder(
//                """{"type":"playerLeftRoom","roomId":"2c8ab43e-5daa-4b3f-9aec-dd573d228521","playerId":"a6b716b5-8545-45df-9377-ed682918747f"}""",
//                """{"type":"roomSelectionChanged","roomId":"2c8ab43e-5daa-4b3f-9aec-dd573d228521","selections":{},"min":null,"max":null,"avg":null}""",
//            )
//        }
//    }

    private fun ReceiveChannel<Frame>.purge() = buildList {
        while (true) {
            val message = tryReceive().getOrNull()?.let { (it as? Frame.Text)?.readText() }
            when {
                message != null -> add(Json.decodeFromString<JsonObject>(message))
                else -> return@buildList
            }
        }
    }
}