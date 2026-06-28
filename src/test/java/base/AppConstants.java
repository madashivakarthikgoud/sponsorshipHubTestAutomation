package base;

/**
 * Central repository for all application-level constants used across the test framework.
 *
 * <p>Keeping strings here ensures that a URL structure change in the application
 * requires editing exactly ONE file — not hunting through every test class.
 */
public final class AppConstants {

    private AppConstants() { /* utility class — no instances */ }

    // ── Application URL path segments ─────────────────────────────────────────
    // These are appended to baseUrl (read from config.properties).

    public static final String PATH_LOGIN          = "login";
    public static final String PATH_SIGNUP         = "signup";
    public static final String PATH_DASHBOARD      = "dashboard";
    public static final String PATH_CAMPAIGNS      = "campaigns";
    public static final String PATH_NEW_CAMPAIGN   = "campaigns/new";
    public static final String PATH_EDIT_CAMPAIGN  = "campaigns/edit";

    // ── URL regex patterns (used with ExpectedConditions.urlMatches) ──────────

    /** Matches exactly the /campaigns list page, e.g. https://host/campaigns */
    public static final String URL_REGEX_CAMPAIGNS_LIST  = ".*/" + PATH_CAMPAIGNS + "$";

    /** Matches a campaign detail page, e.g. https://host/campaigns/42 */
    public static final String URL_REGEX_CAMPAIGN_DETAIL = ".*/" + PATH_CAMPAIGNS + "/\\d+$";

    // ── Driver / wait defaults ────────────────────────────────────────────────
    // These are overridden by config.properties values when present.

    public static final int    DEFAULT_WAIT_TIMEOUT_SECONDS = 30;
    public static final int    DEFAULT_POLL_INTERVAL_SECONDS = 1;
    public static final String DEFAULT_BASE_URL = "https://sponsorship-front.netlify.app/";

    // ── Search test configuration ─────────────────────────────────────────────

    /**
     * Number of leading characters used as the search term in the search-by-name test.
     * Using a partial term validates that the search feature supports substring matching.
     */
    public static final int SEARCH_TERM_PREFIX_LENGTH = 10;
}
