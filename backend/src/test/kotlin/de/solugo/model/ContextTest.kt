package de.solugo.model

import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.should
import io.ktor.websocket.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.test.runTest
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
    fun joinRoomParticipant1() = runTest {
            context.joinRoom(
                roomId = roomId,
                participantId = participant1Id,
                channel = participant1Channel,
                name = "Player 1",
            )

            participant1Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "participantJoinedRoom")
                    put("roomId", roomId)
                    put("participantId", participant1Id)
                    put("name", "Player 1")
                    put("role", "PLAYER")
                },
                buildJsonObject {
                    put("type", "roomSelectionChanged")
                    put("roomId", roomId)
                    put("visible", false)
                    put("selections", buildJsonObject {
                        put(participant1Id, JsonNull)
                    })
                },
                buildJsonObject {
                    put("type", "roomInfoChanged")
                    put("roomId", roomId)
                    put("name", JsonNull)
                    put("options", buildJsonObject {
                        put("½", 0.5)
                        put("1", 1.0)
                        put("2", 2.0)
                        put("3", 3.0)
                        put("5", 5.0)
                        put("8", 8.0)
                        put("13", 13.0)
                        put("21", 21.0)
                        put("♾", JsonNull)
                        put("☕", JsonNull)
                        put("⚡", JsonNull)
                    })
                },
            )
    }

    @Test
    @Order(3)
    fun updateParticipant1Selection() = runTest {
            context.updateParticipantSelection(roomId, participant1Id, "½")

            participant1Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "roomSelectionChanged")
                    put("roomId", roomId)
                    put("visible", false)
                    put("selections", buildJsonObject {
                        put(participant1Id, "½")
                    })
                },
            )
    }

    @Test
    @Order(4)
    fun updateRoomWrongInfo() = runTest {
            context.updateRoomInfo(roomId, name = "Wrong Room")

            participant1Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "roomInfoChanged")
                    put("roomId", roomId)
                    put("name", "Wrong Room")
                    put("options", buildJsonObject {
                        put("½", 0.5)
                        put("1", 1.0)
                        put("2", 2.0)
                        put("3", 3.0)
                        put("5", 5.0)
                        put("8", 8.0)
                        put("13", 13.0)
                        put("21", 21.0)
                        put("♾", JsonNull)
                        put("☕", JsonNull)
                        put("⚡", JsonNull)
                    })
                },
            )
    }

    @Test
    @Order(5)
    fun updateRoomRightInfo() = runTest {
            context.updateRoomInfo(roomId, name = "Right Room", options = mapOf("1" to 1.0, "2" to 2.0))

            participant1Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "roomInfoChanged")
                    put("roomId", roomId)
                    put("name", "Right Room")
                    put("options", buildJsonObject {
                        put("1", 1.0)
                        put("2", 2.0)
                    })
                },
                buildJsonObject {
                    put("type", "roomSelectionChanged")
                    put("roomId", roomId)
                    put("visible", false)
                    put("selections", buildJsonObject {
                        put(participant1Id, JsonNull)
                    })
                },
            )
    }

    @Test
    @Order(6)
    fun updateParticipant1SelectionAfterOptionChange() = runTest {
            context.updateParticipantSelection(roomId, participant1Id, selection = "2")

            participant1Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "roomSelectionChanged")
                    put("roomId", roomId)
                    put("visible", false)
                    put("selections", buildJsonObject {
                        put(participant1Id, "2")
                    })
                },
            )
    }

    @Test
    @Order(7)
    fun joinRoomParticipant2() = runTest {
            context.joinRoom(
                roomId = roomId,
                participantId = participant2Id,
                channel = participant2Channel,
                name = "Player 2",
            )

            participant1Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "participantJoinedRoom")
                    put("roomId", roomId)
                    put("participantId", participant2Id)
                    put("name", "Player 2")
                    put("role", "PLAYER")

                },
                buildJsonObject {
                    put("type", "roomSelectionChanged")
                    put("roomId", roomId)
                    put("visible", false)
                    put("selections", buildJsonObject {
                        put(participant1Id, "2")
                        put(participant2Id, JsonNull)
                    })
                },
            )

            participant2Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "participantJoinedRoom")
                    put("roomId", roomId)
                    put("participantId", participant1Id)
                    put("name", "Player 1")
                    put("role", "PLAYER")
                },
                buildJsonObject {
                    put("type", "participantJoinedRoom")
                    put("roomId", roomId)
                    put("participantId", participant2Id)
                    put("name", "Player 2")
                    put("role", "PLAYER")

                },
                buildJsonObject {
                    put("type", "roomSelectionChanged")
                    put("roomId", roomId)
                    put("visible", false)
                    put("selections", buildJsonObject {
                        put(participant1Id, "")
                        put(participant2Id, JsonNull)
                    })
                },
                buildJsonObject {
                    put("type", "roomInfoChanged")
                    put("roomId", roomId)
                    put("name", "Right Room")
                    put("options", buildJsonObject {
                        put("1", 1.0)
                        put("2", 2.0)
                    })
                },
            )
    }

    @Test
    @Order(9)
    fun updateParticipant2Selection() = runTest {
            context.updateParticipantSelection(roomId, participant2Id, "1")

            participant1Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "roomSelectionChanged")
                    put("roomId", roomId)
                    put("visible", false)
                    put("selections", buildJsonObject {
                        put(participant1Id, "2")
                        put(participant2Id, "")
                    })
                },
            )

            participant2Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "roomSelectionChanged")
                    put("roomId", roomId)
                    put("visible", false)
                    put("selections", buildJsonObject {
                        put(participant1Id, "")
                        put(participant2Id, "1")
                    })
                },
            )
    }

    @Test
    @Order(10)
    fun joinRoomParticipant3() = runTest {
            context.joinRoom(
                roomId = roomId,
                participantId = participant3Id,
                channel = participant3Channel,
                name = "Player 3",
            )

            participant1Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "participantJoinedRoom")
                    put("roomId", roomId)
                    put("participantId", participant3Id)
                    put("name", "Player 3")
                    put("role", "PLAYER")

                },
                buildJsonObject {
                    put("type", "roomSelectionChanged")
                    put("roomId", roomId)
                    put("visible", false)
                    put("selections", buildJsonObject {
                        put(participant1Id, "2")
                        put(participant2Id, "")
                        put(participant3Id, JsonNull)
                    })
                },
            )

            participant2Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "participantJoinedRoom")
                    put("roomId", roomId)
                    put("participantId", participant3Id)
                    put("name", "Player 3")
                    put("role", "PLAYER")

                },
                buildJsonObject {
                    put("type", "roomSelectionChanged")
                    put("roomId", roomId)
                    put("visible", false)
                    put("selections", buildJsonObject {
                        put(participant1Id, "")
                        put(participant2Id, "1")
                        put(participant3Id, JsonNull)
                    })
                },
            )

            participant3Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "participantJoinedRoom")
                    put("roomId", roomId)
                    put("participantId", participant1Id)
                    put("name", "Player 1")
                    put("role", "PLAYER")

                },
                buildJsonObject {
                    put("type", "participantJoinedRoom")
                    put("roomId", roomId)
                    put("participantId", participant2Id)
                    put("name", "Player 2")
                    put("role", "PLAYER")
                },
                buildJsonObject {
                    put("type", "participantJoinedRoom")
                    put("roomId", roomId)
                    put("participantId", participant3Id)
                    put("name", "Player 3")
                    put("role", "PLAYER")
                },
                buildJsonObject {
                    put("type", "roomSelectionChanged")
                    put("roomId", roomId)
                    put("visible", false)
                    put("selections", buildJsonObject {
                        put(participant1Id, "")
                        put(participant2Id, "")
                        put(participant3Id, JsonNull)
                    })
                },
                buildJsonObject {
                    put("type", "roomInfoChanged")
                    put("roomId", roomId)
                    put("name", "Right Room")
                    put("options", buildJsonObject {
                        put("1", 1.0)
                        put("2", 2.0)
                    })
                },
            )
    }

    @Test
    @Order(11)
    fun updateParticipant3Selection() = runTest {
            context.updateParticipantSelection(roomId, participant3Id, "1")

            participant1Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "roomSelectionChanged")
                    put("roomId", roomId)
                    put("visible", false)
                    put("selections", buildJsonObject {
                        put(participant1Id, "2")
                        put(participant2Id, "")
                        put(participant3Id, "")
                    })
                },
            )

            participant2Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "roomSelectionChanged")
                    put("roomId", roomId)
                    put("visible", false)
                    put("selections", buildJsonObject {
                        put(participant1Id, "")
                        put(participant2Id, "1")
                        put(participant3Id, "")
                    })
                },
            )
            participant3Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "roomSelectionChanged")
                    put("roomId", roomId)
                    put("visible", false)
                    put("selections", buildJsonObject {
                        put(participant1Id, "")
                        put(participant2Id, "")
                        put(participant3Id, "1")
                    })
                },
            )
    }

    @Test
    @Order(12)
    fun leaveRoomParticipant1() = runTest {
            context.leaveRoom(roomId, participant1Id)

            participant1Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "participantLeftRoom")
                    put("roomId", roomId)
                    put("participantId", participant1Id)
                },
            )

            participant2Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "participantLeftRoom")
                    put("roomId", roomId)
                    put("participantId", participant1Id)
                },
                buildJsonObject {
                    put("type", "roomSelectionChanged")
                    put("roomId", roomId)
                    put("visible", false)
                    put("selections", buildJsonObject {
                        put(participant2Id, "1")
                        put(participant3Id, "")
                    })
                },
            )
            participant3Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "participantLeftRoom")
                    put("roomId", roomId)
                    put("participantId", participant1Id)
                },
                buildJsonObject {
                    put("type", "roomSelectionChanged")
                    put("roomId", roomId)
                    put("visible", false)
                    put("selections", buildJsonObject {
                        put(participant2Id, "")
                        put(participant3Id, "1")
                    })
                },
            )
    }

    @Test
    @Order(13)
    fun revealRoom() = runTest {
            context.revealRoom(roomId, true)

            participant1Channel.purge() should beEmpty()

            participant2Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "roomSelectionChanged")
                    put("roomId", roomId)
                    put("visible", true)
                    put("selections", buildJsonObject {
                        put(participant2Id, "1")
                        put(participant3Id, "1")
                    })
                },
            )
            participant3Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "roomSelectionChanged")
                    put("roomId", roomId)
                    put("visible", true)
                    put("selections", buildJsonObject {
                        put(participant2Id, "1")
                        put(participant3Id, "1")
                    })
                },
            )
    }

    @Test
    @Order(14)
    fun resetRoom() = runTest {
            context.resetRoom(roomId)

            participant1Channel.purge() should beEmpty()

            participant2Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "roomSelectionChanged")
                    put("roomId", roomId)
                    put("visible", false)
                    put("selections", buildJsonObject {
                        put(participant2Id, JsonNull)
                        put(participant3Id, JsonNull)
                    })
                },
            )
            participant3Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "roomSelectionChanged")
                    put("roomId", roomId)
                    put("visible", false)
                    put("selections", buildJsonObject {
                        put(participant2Id, JsonNull)
                        put(participant3Id, JsonNull)
                    })
                },
            )
    }

    @Test
    @Order(15)
    fun leaveRoomParticipant2() = runTest {
            participant2Channel.close()
            context.removeParticipant(participant2Id)

            participant1Channel.purge() should beEmpty()
            participant2Channel.purge() should beEmpty()

            participant3Channel.purge() shouldContainExactlyInAnyOrder listOf(
                buildJsonObject {
                    put("type", "participantLeftRoom")
                    put("roomId", roomId)
                    put("participantId", participant2Id)
                },
                buildJsonObject {
                    put("type", "roomSelectionChanged")
                    put("roomId", roomId)
                    put("visible", false)
                    put("selections", buildJsonObject {
                        put(participant3Id, JsonNull)
                    })
                },
            )
    }

    @BeforeEach
    fun clear() {
        participant1Channel.purge()
        participant2Channel.purge()
        participant3Channel.purge()
    }

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