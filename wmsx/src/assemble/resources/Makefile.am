SUBDIRS = gstream

install-data-local:
	mkdir -p $(bindir)
	@for f in lcg/*.sh; do \
	$(INSTALL_SCRIPT) $$f $(bindir); \
	done
	@for f in wmsx/*.sh; do \
	$(INSTALL_SCRIPT) $$f $(bindir); \
	done
	mkdir -p $(libexecdir)/wmsx
	cp -pR wmsx/bin $(libexecdir)/wmsx/
	cp -pR wmsx/repo $(libexecdir)/wmsx/
	mkdir -p $(docdir)
	cp -pR wmsx/doc/* $(docdir)
