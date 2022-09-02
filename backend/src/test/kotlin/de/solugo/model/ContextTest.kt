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
    val participant1Id = "c02b43bf-74d2-4bdf-a9f1-23ec8413f485"
    val participant1Channel = Channel<Frame>(Channel.UNLIMITED)
    val participant2Id = "a6b716b5-8545-45df-9377-ed682918747f"
    val participant2Channel = Channel<Frame>(Channel.UNLIMITED)
    val participant3Id = "4642edad-dfa5-4211-8b76-bac9572d329c"
    val participant3Channel = Channel<Frame>(Channel.UNLIMITED)

    @Test
    @Order(1)
    fun joinRoomParticipant1() {
        runBlocking {
            context.joinRoom(
                roomId = roomId,
                participantId = participant1Id,
                channel = participant1Channel,
                name = "Player 1",
            )

            assertThat(participant1Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("participantJoinedRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("participantId", JsonPrimitive(participant1Id))
                    put("name", JsonPrimitive("Player 1"))
                    put("role", JsonPrimitive("PLAYER"))
                },
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(participant1Id, JsonNull)
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
    @Order(3)
    fun updateParticipant1Selection() {
        runBlocking {
            context.updateParticipantSelection(roomId, participant1Id, "½")

            assertThat(participant1Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(participant1Id, JsonPrimitive("½"))
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

            assertThat(participant1Channel.purge()).containsExactlyInAnyOrder(
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

            assertThat(participant1Channel.purge()).containsExactlyInAnyOrder(
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
                        put(participant1Id, JsonNull)
                    })
                },
            )
        }
    }

    @Test
    @Order(6)
    fun updateParticipant1SelectionAfterOptionChange() {
        runBlocking {
            context.updateParticipantSelection(roomId, participant1Id, selection = "2")

            assertThat(participant1Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(participant1Id, JsonPrimitive("2"))
                    })
                },
            )
        }
    }

    @Test
    @Order(7)
    fun joinRoomParticipant2() {
        runBlocking {
            context.joinRoom(
                roomId = roomId,
                participantId = participant2Id,
                channel = participant2Channel,
                name = "Player 2",
            )

            assertThat(participant1Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("participantJoinedRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("participantId", JsonPrimitive(participant2Id))
                    put("name", JsonPrimitive("Player 2"))
                    put("role", JsonPrimitive("PLAYER"))

                },
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(participant1Id, "2")
                        put(participant2Id, JsonNull)
                    })
                },
            )

            assertThat(participant2Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("participantJoinedRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("participantId", JsonPrimitive(participant1Id))
                    put("name", JsonPrimitive("Player 1"))
                    put("role", JsonPrimitive("PLAYER"))
                },
                buildJsonObject {
                    put("type", JsonPrimitive("participantJoinedRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("participantId", JsonPrimitive(participant2Id))
                    put("name", JsonPrimitive("Player 2"))
                    put("role", JsonPrimitive("PLAYER"))

                },
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(participant1Id, "")
                        put(participant2Id, JsonNull)
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
    @Order(9)
    fun updateParticipant2Selection() {
        runBlocking {
            context.updateParticipantSelection(roomId, participant2Id, "1")

            assertThat(participant1Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(participant1Id, "2")
                        put(participant2Id, "")
                    })
                },
            )

            assertThat(participant2Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(participant1Id, "")
                        put(participant2Id, "1")
                    })
                },
            )
        }
    }

    @Test
    @Order(10)
    fun joinRoomParticipant3() {
        runBlocking {
            context.joinRoom(
                roomId = roomId,
                participantId = participant3Id,
                channel = participant3Channel,
                name = "Player 3",
            )

            assertThat(participant1Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("participantJoinedRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("participantId", JsonPrimitive(participant3Id))
                    put("name", JsonPrimitive("Player 3"))
                    put("role", JsonPrimitive("PLAYER"))

                },
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(participant1Id, "2")
                        put(participant2Id, "")
                        put(participant3Id, JsonNull)
                    })
                },
            )

            assertThat(participant2Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("participantJoinedRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("participantId", JsonPrimitive(participant3Id))
                    put("name", JsonPrimitive("Player 3"))
                    put("role", JsonPrimitive("PLAYER"))

                },
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(participant1Id, "")
                        put(participant2Id, "1")
                        put(participant3Id, JsonNull)
                    })
                },
            )

            assertThat(participant3Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("participantJoinedRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("participantId", JsonPrimitive(participant1Id))
                    put("name", JsonPrimitive("Player 1"))
                    put("role", JsonPrimitive("PLAYER"))

                },
                buildJsonObject {
                    put("type", JsonPrimitive("participantJoinedRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("participantId", JsonPrimitive(participant2Id))
                    put("name", JsonPrimitive("Player 2"))
                    put("role", JsonPrimitive("PLAYER"))
                },
                buildJsonObject {
                    put("type", JsonPrimitive("participantJoinedRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("participantId", JsonPrimitive(participant3Id))
                    put("name", JsonPrimitive("Player 3"))
                    put("role", JsonPrimitive("PLAYER"))
                },
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(participant1Id, "")
                        put(participant2Id, "")
                        put(participant3Id, JsonNull)
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
    fun updateParticipant3Selection() {
        runBlocking {
            context.updateParticipantSelection(roomId, participant3Id, "1")

            assertThat(participant1Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(participant1Id, "2")
                        put(participant2Id, "")
                        put(participant3Id, "")
                    })
                },
            )

            assertThat(participant2Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(participant1Id, "")
                        put(participant2Id, "1")
                        put(participant3Id, "")
                    })
                },
            )
            assertThat(participant3Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(participant1Id, "")
                        put(participant2Id, "")
                        put(participant3Id, "1")
                    })
                },
            )
        }
    }

    @Test
    @Order(12)
    fun leaveRoomParticipant1() {
        runBlocking {
            context.leaveRoom(roomId, participant1Id)

            assertThat(participant1Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("participantLeftRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("participantId", JsonPrimitive(participant1Id))
                },
            )

            assertThat(participant2Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("participantLeftRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("participantId", JsonPrimitive(participant1Id))
                },
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(participant2Id, "1")
                        put(participant3Id, "")
                    })
                },
            )
            assertThat(participant3Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("participantLeftRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("participantId", JsonPrimitive(participant1Id))
                },
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(participant2Id, "")
                        put(participant3Id, "1")
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

            assertThat(participant1Channel.purge()).isEmpty()

            assertThat(participant2Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(true))
                    put("selections", buildJsonObject {
                        put(participant2Id, "1")
                        put(participant3Id, "1")
                    })
                },
            )
            assertThat(participant3Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(true))
                    put("selections", buildJsonObject {
                        put(participant2Id, "1")
                        put(participant3Id, "1")
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

            assertThat(participant1Channel.purge()).isEmpty()

            assertThat(participant2Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(participant2Id, JsonNull)
                        put(participant3Id, JsonNull)
                    })
                },
            )
            assertThat(participant3Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(participant2Id, JsonNull)
                        put(participant3Id, JsonNull)
                    })
                },
            )
        }
    }

    @Test
    @Order(15)
    fun leaveRoomParticipant2() {
        runBlocking {
            participant2Channel.close()
            context.removeParticipant(participant2Id)

            assertThat(participant1Channel.purge()).isEmpty()
            assertThat(participant2Channel.purge()).isEmpty()

            assertThat(participant3Channel.purge()).containsExactlyInAnyOrder(
                buildJsonObject {
                    put("type", JsonPrimitive("participantLeftRoom"))
                    put("roomId", JsonPrimitive(roomId))
                    put("participantId", JsonPrimitive(participant2Id))
                },
                buildJsonObject {
                    put("type", JsonPrimitive("roomSelectionChanged"))
                    put("roomId", JsonPrimitive(roomId))
                    put("visible", JsonPrimitive(false))
                    put("selections", buildJsonObject {
                        put(participant3Id, JsonNull)
                    })
                },
            )
        }
    }

    @BeforeEach
    fun clear() {
        participant1Channel.purge()
        participant2Channel.purge()
        participant3Channel.purge()
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
//            participant2Channel.close()
//            context.removeParticipant(participant2Id)
//
//            assertThat(participant1Channel.purge()).isEmpty()
//
//            assertThat(participant2Channel.purge()).isEmpty()
//
//            assertThat(participant3Channel.purge()).containsExactlyInAnyOrder(
//                """{"type":"participantLeftRoom","roomId":"2c8ab43e-5daa-4b3f-9aec-dd573d228521","participantId":"a6b716b5-8545-45df-9377-ed682918747f"}""",
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