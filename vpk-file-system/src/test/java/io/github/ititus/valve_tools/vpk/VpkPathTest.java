package io.github.ititus.valve_tools.vpk;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VpkPathTest {

    @Nested
    class NameCount {

        @Test
        void root() {
            assertThat(new VpkPath(null, "/").getNameCount()).isEqualTo(0);
        }

        @Test
        void absoluteSingle() {
            assertThat(new VpkPath(null, "/a").getNameCount()).isEqualTo(1);
        }

        @Test
        void absoluteMulti() {
            assertThat(new VpkPath(null, "/a/b").getNameCount()).isEqualTo(2);
        }

        @Test
        void emptyRelative() {
            assertThat(new VpkPath(null, "").getNameCount()).isEqualTo(1);
        }

        @Test
        void singleRelative() {
            assertThat(new VpkPath(null, "a").getNameCount()).isEqualTo(1);
        }

        @Test
        void multiRelative() {
            assertThat(new VpkPath(null, "a/b").getNameCount()).isEqualTo(2);
        }
    }

    @Nested
    class Normalize {

        @Nested
        class Neutral {

            @Test
            void root() {
                assertThat(new VpkPath(null, "/").normalize()).isEqualTo(new VpkPath(null, "/"));
            }

            @Test
            void absoluteSingle() {
                assertThat(new VpkPath(null, "/a").normalize()).isEqualTo(new VpkPath(null, "/a"));
            }

            @Test
            void absoluteMulti() {
                assertThat(new VpkPath(null, "/a/b").normalize()).isEqualTo(new VpkPath(null, "/a/b"));
            }

            @Test
            void emptyRelative() {
                assertThat(new VpkPath(null, "").normalize()).isEqualTo(new VpkPath(null, ""));
            }

            @Test
            void singleRelative() {
                assertThat(new VpkPath(null, "a").normalize()).isEqualTo(new VpkPath(null, "a"));
            }

            @Test
            void multiRelative() {
                assertThat(new VpkPath(null, "a/b").normalize()).isEqualTo(new VpkPath(null, "a/b"));
            }
        }

        @Nested
        class Single {

            @Test
            void absoluteSingleDot() {
                assertThat(new VpkPath(null, "/.").normalize()).isEqualTo(new VpkPath(null, "/"));
            }

            @Test
            void absoluteMultiDot() {
                assertThat(new VpkPath(null, "/./a/././b/.").normalize()).isEqualTo(new VpkPath(null, "/a/b"));
            }

            @Test
            void dotRelative() {
                assertThat(new VpkPath(null, ".").normalize()).isEqualTo(new VpkPath(null, ""));
            }

            @Test
            void dotSingleRelative1() {
                assertThat(new VpkPath(null, "./a").normalize()).isEqualTo(new VpkPath(null, "a"));
            }

            @Test
            void dotSingleRelative2() {
                assertThat(new VpkPath(null, "a/.").normalize()).isEqualTo(new VpkPath(null, "a"));
            }

            @Test
            void dotMultiRelative1() {
                assertThat(new VpkPath(null, "./a/b").normalize()).isEqualTo(new VpkPath(null, "a/b"));
            }

            @Test
            void dotMultiRelative2() {
                assertThat(new VpkPath(null, "a/./b").normalize()).isEqualTo(new VpkPath(null, "a/b"));
            }

            @Test
            void dotMultiRelative3() {
                assertThat(new VpkPath(null, "a/b/.").normalize()).isEqualTo(new VpkPath(null, "a/b"));
            }

            @Test
            void dotMultiRelative4() {
                assertThat(new VpkPath(null, "./a/././b/.").normalize()).isEqualTo(new VpkPath(null, "a/b"));
            }
        }

        @Nested
        class Double {

            @Test
            void absoluteDoubleDot1() {
                assertThat(new VpkPath(null, "/a/..").normalize()).isEqualTo(new VpkPath(null, "/"));
            }

            @Test
            void absoluteDoubleDot2() {
                assertThat(new VpkPath(null, "/a/b/../c").normalize()).isEqualTo(new VpkPath(null, "/a/c"));
            }

            @Test
            void absoluteDoubleDotSkip1() {
                assertThat(new VpkPath(null, "/..").normalize()).isEqualTo(new VpkPath(null, "/.."));
            }

            @Test
            void absoluteDoubleDotSkip2() {
                assertThat(new VpkPath(null, "/../a").normalize()).isEqualTo(new VpkPath(null, "/../a"));
            }

            @Test
            void relativeDoubleDot1() {
                assertThat(new VpkPath(null, "a/..").normalize()).isEqualTo(new VpkPath(null, ""));
            }

            @Test
            void relativeDoubleDot2() {
                assertThat(new VpkPath(null, "a/b/../c").normalize()).isEqualTo(new VpkPath(null, "a/c"));
            }

            @Test
            void relativeDoubleDot3() {
                assertThat(new VpkPath(null, "a/b/../..").normalize()).isEqualTo(new VpkPath(null, ""));
            }

            @Test
            void relativeDoubleDot4() {
                assertThat(new VpkPath(null, "a/b/../../c/../d").normalize()).isEqualTo(new VpkPath(null, "d"));
            }

            @Test
            void relativeDoubleDot5() {
                assertThat(new VpkPath(null, "a/b/../../../c").normalize()).isEqualTo(new VpkPath(null, "../c"));
            }

            @Test
            void relativeDoubleDotSkip1() {
                assertThat(new VpkPath(null, "..").normalize()).isEqualTo(new VpkPath(null, ".."));
            }

            @Test
            void relativeDoubleDotSkip2() {
                assertThat(new VpkPath(null, "../a").normalize()).isEqualTo(new VpkPath(null, "../a"));
            }
        }
    }

    @Nested
    class Relativize {

        @Test
        void relativizeAbsolute1() {
            assertThat(new VpkPath(null, "/a/b").relativize(new VpkPath(null, "/a/b/c/d"))).isEqualTo(new VpkPath(null, "c/d"));
        }

        @Test
        void relativizeAbsolute2() {
            assertThat(new VpkPath(null, "/a/b").relativize(new VpkPath(null, "/a/x"))).isEqualTo(new VpkPath(null, "../x"));
        }

        @Test
        void relativizeAbsolute3() {
            assertThat(new VpkPath(null, "/a/b").relativize(new VpkPath(null, "/a/b"))).isEqualTo(new VpkPath(null, ""));
        }

        @Test
        void relativizeAbsolute4() {
            assertThat(new VpkPath(null, "/").relativize(new VpkPath(null, "/a/b"))).isEqualTo(new VpkPath(null, "a/b"));
        }

        @Test
        void relativizeAbsolute5() {
            assertThat(new VpkPath(null, "/a/b").relativize(new VpkPath(null, "/"))).isEqualTo(new VpkPath(null, "../.."));
        }

        @Test
        void relativizeRelative1() {
            assertThat(new VpkPath(null, "a/b").relativize(new VpkPath(null, "a/b/c/d"))).isEqualTo(new VpkPath(null, "c/d"));
        }

        @Test
        void relativizeRelative2() {
            assertThat(new VpkPath(null, "a/b").relativize(new VpkPath(null, "a/x"))).isEqualTo(new VpkPath(null, "../x"));
        }

        @Test
        void relativizeRelative3() {
            assertThat(new VpkPath(null, "a/b").relativize(new VpkPath(null, "a/b"))).isEqualTo(new VpkPath(null, ""));
        }

        @Test
        void relativizeRelative4() {
            assertThat(new VpkPath(null, "").relativize(new VpkPath(null, "a/b"))).isEqualTo(new VpkPath(null, "a/b"));
        }

        @Test
        void relativizeRelative5() {
            assertThat(new VpkPath(null, "a/b").relativize(new VpkPath(null, ""))).isEqualTo(new VpkPath(null, "../.."));
        }
    }
}
