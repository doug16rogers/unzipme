#include <stdio.h>

int main(int argc, char *argv[]) {
    printf("hello\n");
    printf("  argc=%u\n", argc);
    for (int i = 0; i < argc; i++) {
        printf("  argv[%u] = \"%s\"\n", i, argv[i]);
    }
    return 0;
}
