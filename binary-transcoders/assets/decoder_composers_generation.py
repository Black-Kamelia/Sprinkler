import os
from typing import IO

_INTERFACES_COUNT = 16

_RELATIVE_PATH = "./../src/main/kotlin/com/kamelia/sprinkler/binary/decoder/composer"

_FILE_NAME = "DecoderComposerContexts.kt"

# region Templates
_FILE_HEADER = """@file:HideFromJava

package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.zwendo.restrikt.annotation.HideFromJava


"""

_INTERFACE_TEMPLATE = "sealed interface Context"


def _compute_generic(index: int, no_index_on_last: bool) -> str:
    res = ""
    for j in range(index):
        if j + 1 >= index:
            if no_index_on_last:
                res += f"T"
            else:
                res += f"T{index}"
            break
        res += f"T{j + 1}, "

    return res


def _compute_interface_type(index: int, gene: str) -> str:
    gene = f"<{gene}>" if index else ""
    return f"Context{index}{gene}"


def _then_function(f: IO, is_lambda: bool, gene: str, i_type: str, next_i_type: str):
    arg_type = "Decoder<R>"
    arg_type = f"() -> {arg_type}" if is_lambda else arg_type
    gene = f"{gene}, " if gene else ""

    # write @JvmName
    f.write(f'@JvmName("then{i}")\n')
    # header
    f.write(f"fun <T, {gene}R> DecoderComposer<T, {i_type}>.then(nextDecoder: {arg_type})")
    f.write(f": DecoderComposer<R, {next_i_type}> {'{'}\n")
    # body
    f.write("    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)\n")
    f.write("    return DecoderComposer.createFrom(this, next)\n")
    f.write("}\n\n")


def _finally_function(f: IO,  gene: str, i_type: str, index: int):
    gene = f"{gene}, " if gene else ""
    next = ", ".join(("next()" for _ in range(index)))
    if next:
        next += ", "

    # write @JvmName
    f.write(f'@JvmName("finally{i}")\n')
    # header
    f.write(f"fun <T, {gene}R> DecoderComposer<T, {i_type}>.finally(block: ({gene}T) -> R)")
    f.write(f": DecoderComposer<R, Context0> {'{'}\n")
    # body
    f.write("    val next = DecoderComposerUtils.finallyDecoder(this) {\n")
    f.write("        DecoderComposerUtils.ContextIterator(this).run {\n")
    f.write(f"           block({next}it)\n")
    f.write("        }\n")
    f.write("   }\n")
    f.write("    return DecoderComposer.createFrom(this, next)\n")
    f.write("}\n\n")


if __name__ == "__main__":
    with open(os.path.join(_RELATIVE_PATH, _FILE_NAME), "w") as file:
        file.write(_FILE_HEADER)  # write file header
        for i in range(_INTERFACES_COUNT):
            generic = _compute_generic(i, False)
            interface_type = _compute_interface_type(i, generic)
            next_generic = _compute_generic(i + 1, True)
            next_interface_type = _compute_interface_type(i + 1, next_generic)

            # write interface
            interface_header = f"{_INTERFACE_TEMPLATE}{i}"
            if i > 0:
                interface_header += f"<{generic}>"
            interface_header += "\n\n"
            file.write(interface_header)

            # write then function
            if i < _INTERFACES_COUNT - 1:
                _then_function(file, False, generic, interface_type, next_interface_type)
                _then_function(file, True, generic, interface_type, next_interface_type)

            _finally_function(file, generic, interface_type, i)

        # write empty line
        file.write("\n")
