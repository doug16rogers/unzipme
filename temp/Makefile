CFLAGS = -Wall -Werror
CC = gcc
MINGW_CC = /usr/bin/x86_64-w64-mingw32-gcc-10-win32

LINUX_X64 = linux-x86_64
WINDOWS_X64 = windows-x86_64
LINUX_X64_DIR = $(LINUX_X64)
WINDOWS_X64_DIR = $(WINDOWS_X64)

RESOURCES_DIR = ../resources/hello
TARGETS = $(addprefix $(RESOURCES_DIR)/,hello-$(LINUX_X64).zip hello-$(WINDOWS_X64).zip)

all: install

.PHONY: install
install: $(TARGETS)

$(RESOURCES_DIR)/%: %
	cp -f $< $@

hello-$(LINUX_X64).zip: $(LINUX_X64_DIR)/hello_args
	zip $@ $^

hello-$(WINDOWS_X64).zip: $(WINDOWS_X64_DIR)/hello_args.exe
	zip $@ $^

$(LINUX_X64_DIR)/hello_args: hello_args.c
	@mkdir -p $(@D)
	$(CC) -o $@ $(CFLAGS) $^

$(WINDOWS_X64_DIR)/hello_args.exe: hello_args.c
	@mkdir -p $(@D)
	$(MINGW_CC) -o $@ $(CFLAGS) $^

.PHONY: clean
clean:
	rm -f *.o $(TARGETS) $(LINUX_X64_DIR) $(WINDOWS_X64_DIR)
