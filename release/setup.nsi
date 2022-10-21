; Script generated with the Venis Install Wizard

; Define release path
!define RELEASE_DIR "."

; Define your application name
!define PROJECTNAME "APK Scanner"
!define PROJECTNAMEANDVERSION "APK Scanner 2.10"

; Main Install settings
Name "${PROJECTNAMEANDVERSION}"
InstallDir "$PROGRAMFILES64\APKScanner"
InstallDirRegKey HKLM "Software\${PROJECTNAME}" ""
OutFile "APKScanner_install.exe"

; Use compression
SetCompressor Zlib

; Modern interface settings
!include "MUI.nsh"

!define MUI_ABORTWARNING
!define MUI_FINISHPAGE_RUN "$INSTDIR\ApkScanner.exe"

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

; Set languages (first is default language)
!insertmacro MUI_LANGUAGE "English"
!insertmacro MUI_LANGUAGE "Korean"
!insertmacro MUI_RESERVEFILE_LANGDLL

LangString APP_NAME ${LANG_ENGLISH} "APK Scanner"
LangString APP_NAME ${LANG_KOREAN} "APK ��ĳ��"
LangString APP_NAME_DESC ${LANG_ENGLISH} "APK Scanner"
LangString APP_NAME_DESC ${LANG_KOREAN} "APK ��ĳ��"
LangString ASSOCITATE_APK ${LANG_ENGLISH} "Associate APK File"
LangString ASSOCITATE_APK ${LANG_KOREAN} "APK���� ����"
LangString ASSOCITATE_APK_DESC ${LANG_ENGLISH} "Associate APK File. Open apk file by double click."
LangString ASSOCITATE_APK_DESC ${LANG_KOREAN} "APK���� �����մϴ�. APK ������ ����Ŭ���Ͽ� �м� �Ҽ� �ֽ��ϴ�."
LangString ADD_STARTMENU ${LANG_ENGLISH} "Start Menu Shortcuts"
LangString ADD_STARTMENU ${LANG_KOREAN} "���۸޴��� �߰�"
LangString ADD_STARTMENU_DESC ${LANG_ENGLISH} "Start Menu Shortcuts"
LangString ADD_STARTMENU_DESC ${LANG_KOREAN} "���۸޴��� �ٷΰ��� �������� �߰� �մϴ�."
LangString ADD_DESKTOP ${LANG_ENGLISH} "Desktop Shortcut"
LangString ADD_DESKTOP ${LANG_KOREAN} "����ȭ�鿡 �߰�"
LangString ADD_DESKTOP_DESC ${LANG_ENGLISH} "Desktop Shortcut"
LangString ADD_DESKTOP_DESC ${LANG_KOREAN} "����ȭ�鿡 �ٷΰ��� �������� �߰� �մϴ�."

Section $(APP_NAME) Section1

	; Set Section properties
	SectionIn RO
	SetOverwrite on

	; Delete legacy files
	Delete "$INSTDIR\uninstall.exe"
	Delete "$INSTDIR\APKInfoDlg.jar"
	Delete "$INSTDIR\lib\lib\*"
	Delete "$INSTDIR\lib\lib64\*"
	Delete "$INSTDIR\lib\proxy-vole\*"
	RMDir "$INSTDIR\lib\proxy-vole\"
	RMDir "$INSTDIR\lib\lib64\"
	RMDir "$INSTDIR\lib\lib\"

	; Set Section Files and Shortcuts
	SetOutPath "$INSTDIR"
	File "${RELEASE_DIR}\ApkScanner.jar"
	File "${RELEASE_DIR}\ApkScanner.exe"
	SetOutPath "$INSTDIR\data"
	File /r "${RELEASE_DIR}\data\*"
	SetOutPath "$INSTDIR\lib"
	File /r "${RELEASE_DIR}\lib\*"
	SetOutPath "$INSTDIR\plugin"
	File /NONFATAL /r /x plugins.conf "${RELEASE_DIR}\plugin\*"
	SetOutPath "$INSTDIR\security"
	File /r "${RELEASE_DIR}\security\*"
	SetOutPath "$INSTDIR\tool"
	File /r /x linux /x darwin "${RELEASE_DIR}\tool\*"

	Exec '"cmd.exe" /c icacls "$INSTDIR" /grant Users:(OI)(CI)F'

SectionEnd

Section $(ASSOCITATE_APK) Section2

	; Set Section properties
	SetOverwrite on

	; Set Section Files and Shortcuts
    WriteRegStr HKCR "ApkScanner.apk\CLSID" "" "{E88DCCE0-B7B3-11d1-A9F0-00AA0060FA31}"
    WriteRegStr HKCR "ApkScanner.apk\DefaultIcon" "" "$INSTDIR\ApkScanner.exe,1"
    WriteRegStr HKCR "ApkScanner.apk\OpenWithProgids" "CompressedFolder" ""
    WriteRegExpandStr HKCR "ApkScanner.apk\Shell\Open\Command" "" "$\"$INSTDIR\ApkScanner.exe$\" $\"%1$\""
    WriteRegExpandStr HKCR "ApkScanner.apk\Shell\Install\Command" "" "$\"$INSTDIR\ApkScanner.exe$\" install $\"%1$\""
    WriteRegStr HKCR ".apk" "" "ApkScanner.apk"
    WriteRegStr HKCR ".apex" "" "ApkScanner.apk"

    Exec '"cmd.exe" /c assoc .apk=ApkScanner.apk'
    Exec '"cmd.exe" /c assoc .apex=ApkScanner.apk'

SectionEnd

Section $(ADD_STARTMENU) Section3

	; Set Section properties
	SetOverwrite on

	; Set Section Files and Shortcuts
	CreateDirectory "$SMPROGRAMS\APK Scanner"
	CreateShortCut "$SMPROGRAMS\APK Scanner\$(APP_NAME).lnk" "$INSTDIR\ApkScanner.exe"
	CreateShortCut "$SMPROGRAMS\APK Scanner\Uninstall.lnk" "$INSTDIR\uninstall.exe"

SectionEnd

Section $(ADD_DESKTOP) Section4

	; Set Section properties
	SetOverwrite on

	; Set Section Files and Shortcuts
	CreateShortCut "$DESKTOP\$(APP_NAME).lnk" "$INSTDIR\ApkScanner.exe"

SectionEnd

Section -FinishSection

	WriteRegStr HKLM "Software\${PROJECTNAME}" "" "$INSTDIR"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PROJECTNAME}" "DisplayName" "${PROJECTNAME}"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PROJECTNAME}" "UninstallString" "$INSTDIR\uninstall.exe"
	WriteUninstaller "$INSTDIR\uninstall.exe"

SectionEnd

; Modern install component descriptions
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
	!insertmacro MUI_DESCRIPTION_TEXT ${Section1} $(APP_NAME_DESC)
	!insertmacro MUI_DESCRIPTION_TEXT ${Section2} $(ASSOCITATE_APK_DESC)
	!insertmacro MUI_DESCRIPTION_TEXT ${Section3} $(ADD_STARTMENU_DESC)
	!insertmacro MUI_DESCRIPTION_TEXT ${Section4} $(ADD_DESKTOP_DESC)
!insertmacro MUI_FUNCTION_DESCRIPTION_END

;Uninstall section
Section Uninstall

	;Remove from registry...
	DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PROJECTNAME}"
	DeleteRegKey HKLM "SOFTWARE\${PROJECTNAME}"

	DeleteRegKey HKCR "ApkScanner.apk"

	; Delete self
	Delete "$INSTDIR\uninstall.exe"

	; Delete Shortcuts
	Delete "$SMPROGRAMS\APK Scanner\$(APP_NAME).lnk"
	Delete "$SMPROGRAMS\APK Scanner\Uninstall.lnk"
	Delete "$DESKTOP\$(APP_NAME).lnk"

	; Clean up APK Scanner
	; Remove remaining directories
	RMDir "$SMPROGRAMS\APK Scanner"
	RMDir /r /REBOOTOK "$INSTDIR"

	Var /GLOBAL associate
	ReadRegStr $associate HKCR .apk ""
	DetailPrint "Associate .apk: $associate"
	${If} $associate == "ApkScanner.apk"
		WriteRegStr HKCR ".apk" "" ""
    	ExecWait '"cmd.exe" /c assoc .apk=.apk'
    	Exec '"cmd.exe" /c assoc .apk='
	${EndIf}

	ReadRegStr $associate HKCR .apex ""
	DetailPrint "Associate .apex: $associate"
	${If} $associate == "ApkScanner.apk"
		WriteRegStr HKCR ".apex" "" ""
    	ExecWait '"cmd.exe" /c assoc .apex=.apex'
    	Exec '"cmd.exe" /c assoc .apex='
	${EndIf}

SectionEnd

; On initialization
Function .onInit

	!insertmacro MUI_LANGDLL_DISPLAY

FunctionEnd

; eof