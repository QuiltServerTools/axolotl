package template.extensions

import com.kotlindiscord.kord.extensions.commands.slash.AutoAckType
import com.kotlindiscord.kord.extensions.extensions.Extension
import dev.kord.common.annotation.KordPreview
import template.TEST_SERVER_ID
@OptIn(KordPreview::class)
class CommandTest: Extension() {
    override val name = "test"

    override suspend fun setup() {

        slashCommand {
            name = "test"
            description = "Do not question the sacred tests"

            // We want to send a public follow-up - KordEx will handle the rest
            autoAck = AutoAckType.PUBLIC

            guild(TEST_SERVER_ID)  // Otherwise it'll take an hour to update

            action {
                // Because of the DslMarker annotation KordEx uses, we need to grab Kord explicitly
                val kord = this@CommandTest.kord

                publicFollowUp {
                    content = "Test Successful"
                }
            }
        }
    }
}
