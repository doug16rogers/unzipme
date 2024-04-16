CC = gcc
CFLAGS = -Wall -Werror
TARGETS = hello_args

OS_BASE_DIR = ../resources/os
OS_NAME := $(shell uname -s | tr '[[:upper:]]' '[[:lower:]]')_$(shell uname -m)
OS_DIR = $(OS_BASE_DIR)/$(OS_NAME)
OS_BIN = $(OS_DIR)/bin
OS_BIN_TARGETS = $(addprefix $(OS_BIN)/,$(TARGETS))

all: install

.PHONY: install
install: $(OS_BIN_TARGETS)

$(OS_BIN)/%: %
	cp -f $< $@

hello_args: hello_args.c
	$(CC) -o $@ $(CFLAGS) $^

.PHONY: clean
clean:
	rm -f *.o $(TARGETS) $(OS_BIN_TARGETS)
