import ch.qos.logback.core.joran.spi.ConsoleTarget

def environment = System.getenv().getOrDefault("SENTRY_ENVIRONMENT", "dev")
def defaultLevel = INFO

if (environment == "dev") {
    defaultLevel = DEBUG

    // Silence warning about missing native PRNG on Windows
    logger("io.ktor.util.random", ERROR)
    // Silence warnings about missing translations (we aren't using them)
    logger("com.kotlindiscord.kord.extensions.i18n.ResourceBundleTranslations", ERROR)
    // Silence JGit
    logger("org.eclipse.jgit", INFO)
}

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%magenta([%d{YYYY-MM-dd HH:mm:ss.SSS}]) %cyan([%thread]) %highlight(%-5level) %yellow(%logger{36}) - %msg%n"
    }

    target = ConsoleTarget.SystemOut
}

root(defaultLevel, ["CONSOLE"])
