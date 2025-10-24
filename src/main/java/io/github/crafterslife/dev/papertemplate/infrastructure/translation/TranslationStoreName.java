package io.github.crafterslife.dev.papertemplate.infrastructure.translation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;

/**
 * {@link net.kyori.adventure.translation.TranslationStore} を識別するための一意な名前を格納するアノテーション。
 */
@Retention(RetentionPolicy.RUNTIME)
@NullMarked
public @interface TranslationStoreName {

    /**
     * 名前空間。
     *
     * @return 名前空間
     * @see Key#namespace()
     */
    String namespace() default "";

    /**
     * 値。
     *
     * @return 値
     * @see Key#value()
     */
    String value();
}
