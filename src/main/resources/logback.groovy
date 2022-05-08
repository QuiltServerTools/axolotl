import ch.qos.logback.core.joran.spi.ConsoleTarget

def environment = System.getenv().getOrDefault("SENTRY_ENVIRONMENT", "dev")

def defaultLevel = INFO
def defaultTarget = ConsoleTarget.SystemErr

if (environment == "dev") {
    defaultLevel = DEBUG
    defaultTarget = ConsoleTarget.SystemOut

    // Silence warning about missing native PRNG on Windows
    logger("io.ktor.util.random", ERROR)
    // Silence warnings about missing translations (we aren't using them)
    logger("com.kotlindiscord.kord.extensions.i18n.ResourceBundleTranslations", ERROR)
    // Silence JGit
    logger("org.eclipse.jgit", INFO)
}

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%boldGreen(%d{yyyy-MM-dd}) %boldYellow(%d{HH:mm:ss}) %gray(|) %highlight(%5level) %gray(|) %boldMagenta(%40.40logger{40}) %gray(|) %msg%n"

        withJansi = true
    }

    target = defaultTarget
}

root(defaultLevel, ["CONSOLE"])
