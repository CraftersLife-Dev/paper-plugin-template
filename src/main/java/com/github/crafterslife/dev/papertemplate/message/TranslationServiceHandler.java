/*
 * PaperTemplate
 *
 * Copyright (c) 2025. Namiu/うにたろう
 *                     Contributors []
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.crafterslife.dev.papertemplate.message;

import com.github.crafterslife.dev.papertemplate.utility.CaseConverter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.Nullable;

/**
 * TranslationService のメソッド呼び出しを処理する InvocationHandler。
 */
class TranslationServiceHandler implements InvocationHandler {

    TranslationServiceHandler() {

    }

    @Override
    public @Nullable Object invoke(final Object proxy, final Method method, final @Nullable Object[] args) throws InvocationTargetException, IllegalAccessException {
        // Object クラスのメソッド (equals, hashCode, toString) の特殊処理
        if (method.getDeclaringClass().equals(Object.class)) {
            return switch (method.getName()) {
                case "equals" -> proxy == args[0]; // プロキシインスタンスの等価性をチェック
                case "hashCode" -> System.identityHashCode(proxy); // プロキシインスタンスのハッシュコードを返す
                case "toString" -> "Proxy for " + proxy.getClass().getInterfaces()[0].getName(); // わかりやすい文字列を返す
                default -> method.invoke(this, args); // その他のObjectメソッド（通常は発生しない想定）
            };
        }

        // @Message アノテーションからキーを取得
        final MessageKey messageKey = method.getAnnotation(MessageKey.class);
        if (messageKey == null) {
            throw new UnsupportedOperationException("メソッド %s に @MessageKey アノテーションが存在しない".formatted(method.getName()));
        }

        // 第一引数をAudience型へキャスト
        final Optional<Audience> receiver = args[0] instanceof Audience audience
                ? Optional.of(audience)
                : Optional.empty();

        // TagResolverを生成 (第一引数がAudienceだったら第二引数からタグを生成)
        final Parameter[] parameters = method.getParameters();
        final TagResolver tagResolver = IntStream.range(receiver.isEmpty() ? 0 : 1, parameters.length)
                .mapToObj(i -> {
                    final Object value = Objects.requireNonNull(args[i], "value");
                    return TranslationServiceHandler.asTagResolver(parameters[i], value);
                })
                .reduce(TagResolver.builder(), TagResolver.Builder::resolver, (builder, __) -> builder)
                .build();

        // メッセージのレンダリング
        final String translationKey = messageKey.value();
        final Component renderedComponent = TranslationRenderer.render(translationKey, receiver.orElse(null), tagResolver);

        // Component型ならそのまま返し、voidなら第一引数のAudienceへ送信
        final Class<?> returnType = method.getReturnType();
        if (returnType.equals(Component.class)) {
            return renderedComponent;
        } else if (returnType.equals(void.class) || returnType.equals(Void.class)) {
            receiver.ifPresentOrElse(
                    present -> present.sendMessage(renderedComponent),
                    () -> {
                        throw new IllegalArgumentException("戻り値の型がvoidまたはVoidの場合、第一引数 (Audience) はnullであってはならない。");
                    }
            );
            return null;
        } else {
            throw new IllegalArgumentException("不適切な戻り値の型: " + returnType.getName() + "。サポートされる型: Component, void/Void");
        }
    }

    @SuppressWarnings("PatternValidation")
    private static TagResolver asTagResolver(final Parameter parameter, final Object value) {
        return switch (value) {
            case TagResolver placeholder -> placeholder;
            case Component component -> Placeholder.component(CaseConverter.toSnakeCase(parameter.getName()), component);
            case String string -> Placeholder.parsed(CaseConverter.toSnakeCase(parameter.getName()), string);
            default -> throw new UnsupportedOperationException("不適切な型。サポート型: [TagResolver.Single, Component, String]。実際の型: " + value.getClass().getName());
        };
    }
}
