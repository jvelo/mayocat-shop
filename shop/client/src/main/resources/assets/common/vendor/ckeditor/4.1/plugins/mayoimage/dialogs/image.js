/**
 * @license Copyright (c) 2003-2013, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.html or http://ckeditor.com/license
 */

(function () {
    var imageDialog = function (editor, dialogType) {
        // Load image preview.
        var IMAGE = 1,
            LINK = 2,
            PREVIEW = 4,
            CLEANUP = 8,
            regexGetSize = /^\s*(\d+)((px)|\%)?\s*$/i,
            regexGetSizeOrEmpty = /(^\s*(\d+)((px)|\%)?\s*$)|^$/i,
            pxLengthRegex = /^\d+px$/;

        var onSizeChange = function () {
            var value = this.getValue(),
            // This = input element.
                dialog = this.getDialog(),
                aMatch = value.match(regexGetSize); // Check value
            if (aMatch) {
                if (aMatch[ 2 ] == '%') // % is allowed - > unlock ratio.
                    switchLockRatio(dialog, false); // Unlock.
                value = aMatch[ 1 ];
            }

            // Only if ratio is locked
            if (dialog.lockRatio) {
                var oImageOriginal = dialog.originalElement;
                if (oImageOriginal.getCustomData('isReady') == 'true') {
                    if (this.id == 'txtHeight') {
                        if (value && value != '0')
                            value = Math.round(oImageOriginal.$.width * ( value / oImageOriginal.$.height ));
                        if (!isNaN(value))
                            dialog.setValueOf('info', 'txtWidth', value);
                    } else //this.id = txtWidth.
                    {
                        if (value && value != '0')
                            value = Math.round(oImageOriginal.$.height * ( value / oImageOriginal.$.width ));
                        if (!isNaN(value))
                            dialog.setValueOf('info', 'txtHeight', value);
                    }
                }
            }
            updatePreview(dialog);
        };

        var updatePreview = function (dialog) {
            //Don't load before onShow.
            if (!dialog.originalElement || !dialog.preview)
                return 1;

            // Read attributes and update imagePreview;
            dialog.commitContent(PREVIEW, dialog.preview);
            return 0;
        };

        // Custom commit dialog logic, where we're intended to give inline style
        // field (txtdlgGenStyle) higher priority to avoid overwriting styles contribute
        // by other fields.
        function commitContent() {
            var args = arguments;
            var inlineStyleField = this.getContentElement('advanced', 'txtdlgGenStyle');
            inlineStyleField && inlineStyleField.commit.apply(inlineStyleField, args);

            this.foreach(function (widget) {
                if (widget.commit && widget.id != 'txtdlgGenStyle')
                    widget.commit.apply(widget, args);
            });
        }

        // Avoid recursions.
        var incommit;

        // Synchronous field values to other impacted fields is required, e.g. border
        // size change should alter inline-style text as well.
        function commitInternally(targetFields) {
            if (incommit)
                return;

            incommit = 1;

            var dialog = this.getDialog(),
                element = dialog.imageElement;
            if (element) {
                // Commit this field and broadcast to target fields.
                this.commit(IMAGE, element);

                targetFields = [].concat(targetFields);
                var length = targetFields.length,
                    field;
                for (var i = 0; i < length; i++) {
                    field = dialog.getContentElement.apply(dialog, targetFields[ i ].split(':'));
                    // May cause recursion.
                    field && field.setup(IMAGE, element);
                }
            }

            incommit = 0;
        }

        var switchLockRatio = function (dialog, value) {
            if (!dialog.getContentElement('info', 'ratioLock'))
                return null;

            var oImageOriginal = dialog.originalElement;

            // Dialog may already closed. (#5505)
            if (!oImageOriginal)
                return null;

            // Check image ratio and original image ratio, but respecting user's preference.
            if (value == 'check') {
                if (!dialog.userlockRatio && oImageOriginal.getCustomData('isReady') == 'true') {
                    var width = dialog.getValueOf('info', 'txtWidth'),
                        height = dialog.getValueOf('info', 'txtHeight'),
                        originalRatio = oImageOriginal.$.width * 1000 / oImageOriginal.$.height,
                        thisRatio = width * 1000 / height;
                    dialog.lockRatio = false; // Default: unlock ratio

                    if (!width && !height)
                        dialog.lockRatio = true;
                    else if (!isNaN(originalRatio) && !isNaN(thisRatio)) {
                        if (Math.round(originalRatio) == Math.round(thisRatio))
                            dialog.lockRatio = true;
                    }
                }
            } else if (value != undefined)
                dialog.lockRatio = value;
            else {
                dialog.userlockRatio = 1;
                dialog.lockRatio = !dialog.lockRatio;
            }

            var ratioButton = CKEDITOR.document.getById(btnLockSizesId);
            if (dialog.lockRatio)
                ratioButton.removeClass('cke_btn_unlocked');
            else
                ratioButton.addClass('cke_btn_unlocked');

            ratioButton.setAttribute('aria-checked', dialog.lockRatio);

            // Ratio button hc presentation - WHITE SQUARE / BLACK SQUARE
            if (CKEDITOR.env.hc) {
                var icon = ratioButton.getChild(0);
                icon.setHtml(dialog.lockRatio ? CKEDITOR.env.ie ? '\u25A0' : '\u25A3' : CKEDITOR.env.ie ? '\u25A1' : '\u25A2');
            }

            return dialog.lockRatio;
        };

        var resetSize = function (dialog) {
            var oImageOriginal = dialog.originalElement;
            if (oImageOriginal.getCustomData('isReady') == 'true') {
                var widthField = dialog.getContentElement('info', 'txtWidth'),
                    heightField = dialog.getContentElement('info', 'txtHeight');
                widthField && widthField.setValue(oImageOriginal.$.width);
                heightField && heightField.setValue(oImageOriginal.$.height);
            }
            updatePreview(dialog);
        };

        var setupDimension = function (type, element) {
            if (type != IMAGE)
                return;

            function checkDimension(size, defaultValue) {
                var aMatch = size.match(regexGetSize);
                if (aMatch) {
                    if (aMatch[ 2 ] == '%') // % is allowed.
                    {
                        aMatch[ 1 ] += '%';
                        switchLockRatio(dialog, false); // Unlock ratio
                    }
                    return aMatch[ 1 ];
                }
                return defaultValue;
            }

            var dialog = this.getDialog(),
                value = '',
                dimension = this.id == 'txtWidth' ? 'width' : 'height',
                size = element.getAttribute(dimension);

            if (size)
                value = checkDimension(size, value);
            value = checkDimension(element.getStyle(dimension), value);

            this.setValue(value);
        };

        var onImgLoadEvent = function () {
            // Image is ready.
            var original = this.originalElement;
            original.setCustomData('isReady', 'true');
            original.removeListener('load', onImgLoadEvent);
            original.removeListener('error', onImgLoadErrorEvent);
            original.removeListener('abort', onImgLoadErrorEvent);

            // New image -> new domensions
            if (!this.dontResetSize)
                resetSize(this);

            if (this.firstLoad)
                CKEDITOR.tools.setTimeout(function () {
                    switchLockRatio(this, 'check');
                }, 0, this);

            this.firstLoad = false;
            this.dontResetSize = false;
        };

        var onImgLoadErrorEvent = function () {
            // Error. Image is not loaded.
            var original = this.originalElement;
            original.removeListener('load', onImgLoadEvent);
            original.removeListener('error', onImgLoadErrorEvent);
            original.removeListener('abort', onImgLoadErrorEvent);

            // Set Error image.
            var noimage = CKEDITOR.getUrl(CKEDITOR.plugins.get('image').path + 'images/noimage.png');

            switchLockRatio(this, false); // Unlock.
        };

        var numbering = function (id) {
                return CKEDITOR.tools.getNextId() + '_' + id;
            },
            btnLockSizesId = numbering('btnLockSizes'),
            btnResetSizeId = numbering('btnResetSize'),
            previewLinkId = numbering('previewLink'),
            mayoProductImgsId = numbering('mayoProductImgs');

        return {
            title: "Insert image",
            minWidth: 620,
            minHeight: 260,
            onShow: function () {
                this.imageElement = false;
                this.linkElement = false;

                // Default: create a new element.
                this.imageEditMode = false;
                this.linkEditMode = false;

                this.lockRatio = true;
                this.userlockRatio = 0;
                this.dontResetSize = false;
                this.firstLoad = true;
                this.addLink = false;

                var editor = this.getParentEditor(),
                    sel = editor.getSelection(),
                    element = sel && sel.getSelectedElement(),
                    link = element && editor.elementPath(element).contains('a', 1);

                // Copy of the image
                this.originalElement = editor.document.createElement('img');
                this.originalElement.setAttribute('alt', '');
                this.originalElement.setCustomData('isReady', 'false');

                if (link) {
                    this.linkElement = link;
                    this.linkEditMode = true;

                    // Look for Image element.
                    var linkChildren = link.getChildren();
                    if (linkChildren.count() == 1) // 1 child.
                    {
                        var childTagName = linkChildren.getItem(0).getName();
                        if (childTagName == 'img' || childTagName == 'input') {
                            this.imageElement = linkChildren.getItem(0);
                            if (this.imageElement.getName() == 'img')
                                this.imageEditMode = 'img';
                            else if (this.imageElement.getName() == 'input')
                                this.imageEditMode = 'input';
                        }
                    }
                    // Fill out all fields.
                    if (dialogType == 'mayoimage')
                        this.setupContent(LINK, link);
                }

                if (element && element.getName() == 'img' && !element.data('cke-realelement') || element && element.getName() == 'input' && element.getAttribute('type') == 'image') {
                    this.imageEditMode = element.getName();
                    this.imageElement = element;
                }

                if (this.imageEditMode) {
                    // Use the original element as a buffer from  since we don't want
                    // temporary changes to be committed, e.g. if the dialog is canceled.
                    this.cleanImageElement = this.imageElement;
                    this.imageElement = this.cleanImageElement.clone(true, true);

                    // Fill out all fields.
                    this.setupContent(IMAGE, this.imageElement);
                } else
                    this.imageElement = editor.document.createElement('img');

                // Refresh LockRatio button
                switchLockRatio(this, true);

            },
            onOk: function () {
                // Edit existing Image.
                if (this.imageEditMode) {
                    var imgTagName = this.imageEditMode;

                    // Image dialog and Input element.
                    if (dialogType == 'mayoimage' && imgTagName == 'input' && confirm(editor.lang.image.button2Img)) {
                        // Replace INPUT-> IMG
                        imgTagName = 'img';
                        this.imageElement = editor.document.createElement('img');
                        this.imageElement.setAttribute('alt', '');
                        editor.insertElement(this.imageElement);
                    }
                    // ImageButton dialog and Image element.
                    else if (dialogType != 'mayoimage' && imgTagName == 'img' && confirm(editor.lang.image.img2Button)) {
                        // Replace IMG -> INPUT
                        imgTagName = 'input';
                        this.imageElement = editor.document.createElement('input');
                        this.imageElement.setAttributes({
                            type: 'image',
                            alt: ''
                        });
                        editor.insertElement(this.imageElement);
                    } else {
                        // Restore the original element before all commits.
                        this.imageElement = this.cleanImageElement;
                        delete this.cleanImageElement;
                    }
                } else // Create a new image.
                {
                    // Image dialog -> create IMG element.
                    if (dialogType == 'mayoimage')
                        this.imageElement = editor.document.createElement('img');
                    else {
                        this.imageElement = editor.document.createElement('input');
                        this.imageElement.setAttribute('type', 'image');
                    }
                    this.imageElement.setAttribute('alt', '');
                }

                // Create a new link.
                if (!this.linkEditMode)
                    this.linkElement = editor.document.createElement('a');

                // Set attributes.
                this.commitContent(IMAGE, this.imageElement);
                this.commitContent(LINK, this.linkElement);

                // Remove empty style attribute.
                if (!this.imageElement.getAttribute('style'))
                    this.imageElement.removeAttribute('style');

                // Insert a new Image.
                if (!this.imageEditMode) {
                    if (this.addLink) {
                        //Insert a new Link.
                        if (!this.linkEditMode) {
                            editor.insertElement(this.linkElement);
                            this.linkElement.append(this.imageElement, false);
                        } else //Link already exists, image not.
                            editor.insertElement(this.imageElement);
                    } else
                        editor.insertElement(this.imageElement);
                } else // Image already exists.
                {
                    //Add a new link element.
                    if (!this.linkEditMode && this.addLink) {
                        editor.insertElement(this.linkElement);
                        this.imageElement.appendTo(this.linkElement);
                    }
                    //Remove Link, Image exists.
                    else if (this.linkEditMode && !this.addLink) {
                        editor.getSelection().selectElement(this.linkElement);
                        editor.insertElement(this.imageElement);
                    }
                }
            },
            onLoad: function () {
                if (dialogType != 'mayoimage')
                    this.hidePage('Link'); //Hide Link tab.
                var doc = this._.element.getDocument();

                if (this.getContentElement('info', 'ratioLock')) {
                    this.addFocusable(doc.getById(btnResetSizeId), 5);
                    this.addFocusable(doc.getById(btnLockSizesId), 5);
                }

                this.commitContent = commitContent;
            },
            onHide: function () {
                if (this.preview)
                    this.commitContent(CLEANUP, this.preview);

                if (this.originalElement) {
                    this.originalElement.removeListener('load', onImgLoadEvent);
                    this.originalElement.removeListener('error', onImgLoadErrorEvent);
                    this.originalElement.removeListener('abort', onImgLoadErrorEvent);
                    this.originalElement.remove();
                    this.originalElement = false; // Dialog is closed.
                }

                delete this.imageElement;
            },
            contents: [
                {
                    id: 'info',
                    label: editor.lang.image.infoTab,
                    accessKey: 'I',
                    elements: [
                        {
                            type: 'vbox',
                            padding: 0,
                            children: [
                                {
                                    id: 'mayoUrl',
                                    type: 'html',
                                    html: '<div id="' + mayoProductImgsId + '"><div style="display:none;">Images</div></div>',
                                    onLoad: function () {
                                        var $imgsDiv = $(document.getElementById(mayoProductImgsId));
                                        $.get(CKEDITOR.config.mayocat_entityUri + 'images', function (data) {
                                            $imgsDiv.children('div:hidden').show();
                                            $.each(data, function (i, v) {
                                                var $el = $('<div>')
                                                    .css({'float': 'left', 'margin': '2px'})
                                                    .append(
                                                        $('<img>')
                                                            .attr({
                                                                src: v.file._href + '?width=90&height=90',
                                                                rel: 'attachment',
                                                                title: v.title
                                                            })
                                                            .css('border', '2px solid white')
                                                            .data('href', v.file._href)
                                                    );
                                                $imgsDiv.append($el);
                                            });
                                            $imgsDiv
                                                .on('click', 'img', function () {
                                                    $imgsDiv.find('img').each(function (i, e) {
                                                        $(this).css('border', '2px solid white');
                                                    })
                                                    $(this).css('border', '2px solid #6ac2bf');
                                                    $imgsDiv.data('selectedHref', $(this).data('href'));
                                                })
                                                .on('mouseenter', 'img', function () {
                                                    $(this).css('cursor', 'pointer');
                                                });
                                        });
                                    },
                                    setup: function (type, element) {
                                        var $imgsDiv = $(document.getElementById(mayoProductImgsId));
                                        if (type == IMAGE) {
                                            var href = element.data('cke-saved-src') || element.getAttribute('src');
                                            $imgsDiv.find('img').each(function (i, e) {
                                                if ($(this).data('href') == href) $(this).css('border', '2px solid #6ac2bf');
                                            })
                                        }
                                    },
                                    commit: function (type, element) {
                                        var $imgsDiv = $(document.getElementById(mayoProductImgsId));
                                        if (type == IMAGE) {
                                            element.data('cke-saved-src', $imgsDiv.data('selectedHref'));
                                            element.setAttribute('src', $imgsDiv.data('selectedHref'));
                                        } else if (type == CLEANUP) {
                                            element.setAttribute('src', ''); // If removeAttribute doesn't work.
                                            element.removeAttribute('src');
                                        }
                                    }
                                }
                            ]
                        },
                        {
                            id: 'txtAlt',
                            type: 'text',
                            label: editor.lang.image.alt,
                            accessKey: 'T',
                            'default': '',
                            onChange: function () {
                                updatePreview(this.getDialog());
                            },
                            setup: function (type, element) {
                                if (type == IMAGE)
                                    this.setValue(element.getAttribute('alt'));
                            },
                            commit: function (type, element) {
                                if (type == IMAGE) {
                                    if (this.getValue() || this.isChanged())
                                        element.setAttribute('alt', this.getValue());
                                } else if (type == PREVIEW) {
                                    element.setAttribute('alt', this.getValue());
                                } else if (type == CLEANUP) {
                                    element.removeAttribute('alt');
                                }
                            }
                        },
                        {
                            type: 'hbox',
                            children: [
                                {
                                    id: 'basic',
                                    type: 'vbox',
                                    children: [
                                        {
                                            type: 'hbox',
                                            style: 'width:100px;margin:0;',
                                            requiredContent: 'img{width,height}',
                                            children: [
                                                {
                                                    type: 'text',
                                                    width: '40px',
                                                    id: 'txtWidth',
                                                    label: editor.lang.common.width,
                                                    onKeyUp: onSizeChange,
                                                    onChange: function () {
                                                        commitInternally.call(this, 'advanced:txtdlgGenStyle');
                                                    },
                                                    validate: function () {
                                                        var aMatch = this.getValue().match(regexGetSizeOrEmpty),
                                                            isValid = !!( aMatch && parseInt(aMatch[ 1 ], 10) !== 0 );
                                                        if (!isValid)
                                                            alert(editor.lang.common.invalidWidth);
                                                        return isValid;
                                                    },
                                                    setup: setupDimension,
                                                    commit: function (type, element, internalCommit) {
                                                        var value = this.getValue();
                                                        if (type == IMAGE) {
                                                            if (value)
                                                                element.setStyle('width', CKEDITOR.tools.cssLength(value));
                                                            else
                                                                element.removeStyle('width');

                                                            !internalCommit && element.removeAttribute('width');
                                                        } else if (type == PREVIEW) {
                                                            var aMatch = value.match(regexGetSize);
                                                            if (!aMatch) {
                                                                var oImageOriginal = this.getDialog().originalElement;
                                                                if (oImageOriginal.getCustomData('isReady') == 'true')
                                                                    element.setStyle('width', oImageOriginal.$.width + 'px');
                                                            } else
                                                                element.setStyle('width', CKEDITOR.tools.cssLength(value));
                                                        } else if (type == CLEANUP) {
                                                            element.removeAttribute('width');
                                                            element.removeStyle('width');
                                                        }
                                                    }
                                                },
                                                {
                                                    type: 'text',
                                                    id: 'txtHeight',
                                                    width: '40px',
                                                    label: editor.lang.common.height,
                                                    onKeyUp: onSizeChange,
                                                    onChange: function () {
                                                        commitInternally.call(this, 'advanced:txtdlgGenStyle');
                                                    },
                                                    validate: function () {
                                                        var aMatch = this.getValue().match(regexGetSizeOrEmpty),
                                                            isValid = !!( aMatch && parseInt(aMatch[ 1 ], 10) !== 0 );
                                                        if (!isValid)
                                                            alert(editor.lang.common.invalidHeight);
                                                        return isValid;
                                                    },
                                                    setup: setupDimension,
                                                    commit: function (type, element, internalCommit) {
                                                        var value = this.getValue();
                                                        if (type == IMAGE) {
                                                            if (value)
                                                                element.setStyle('height', CKEDITOR.tools.cssLength(value));
                                                            else
                                                                element.removeStyle('height');

                                                            !internalCommit && element.removeAttribute('height');
                                                        } else if (type == PREVIEW) {
                                                            var aMatch = value.match(regexGetSize);
                                                            if (!aMatch) {
                                                                var oImageOriginal = this.getDialog().originalElement;
                                                                if (oImageOriginal.getCustomData('isReady') == 'true')
                                                                    element.setStyle('height', oImageOriginal.$.height + 'px');
                                                            } else
                                                                element.setStyle('height', CKEDITOR.tools.cssLength(value));
                                                        } else if (type == CLEANUP) {
                                                            element.removeAttribute('height');
                                                            element.removeStyle('height');
                                                        }
                                                    }
                                                },
                                                {
                                                    id: 'ratioLock',
                                                    type: 'html',
                                                    style: 'width:40px;height:40px;',
                                                    onLoad: function () {
                                                        // Activate Reset button
                                                        var resetButton = CKEDITOR.document.getById(btnResetSizeId),
                                                            ratioButton = CKEDITOR.document.getById(btnLockSizesId);
                                                        if (resetButton) {
                                                            resetButton.on('click', function (evt) {
                                                                resetSize(this);
                                                                evt.data && evt.data.preventDefault();
                                                            }, this.getDialog());
                                                            resetButton.on('mouseover', function () {
                                                                this.addClass('cke_btn_over');
                                                            }, resetButton);
                                                            resetButton.on('mouseout', function () {
                                                                this.removeClass('cke_btn_over');
                                                            }, resetButton);
                                                        }
                                                        // Activate (Un)LockRatio button
                                                        if (ratioButton) {
                                                            ratioButton.on('click', function (evt) {
                                                                var locked = switchLockRatio(this),
                                                                    oImageOriginal = this.originalElement,
                                                                    width = this.getValueOf('info', 'txtWidth');

                                                                if (oImageOriginal.getCustomData('isReady') == 'true' && width) {
                                                                    var height = oImageOriginal.$.height / oImageOriginal.$.width * width;
                                                                    if (!isNaN(height)) {
                                                                        this.setValueOf('info', 'txtHeight', Math.round(height));
                                                                        updatePreview(this);
                                                                    }
                                                                }
                                                                evt.data && evt.data.preventDefault();
                                                            }, this.getDialog());
                                                            ratioButton.on('mouseover', function () {
                                                                this.addClass('cke_btn_over');
                                                            }, ratioButton);
                                                            ratioButton.on('mouseout', function () {
                                                                this.removeClass('cke_btn_over');
                                                            }, ratioButton);
                                                        }
                                                    },
                                                    html: '<div>' +
                                                        '<a href="javascript:void(0)" tabindex="-1" title="' + editor.lang.image.lockRatio +
                                                        '" class="cke_btn_locked" id="' + btnLockSizesId + '" role="checkbox"><span class="cke_icon"></span><span class="cke_label">' + editor.lang.image.lockRatio + '</span></a>' +
                                                        '<a href="javascript:void(0)" tabindex="-1" title="' + editor.lang.image.resetSize +
                                                        '" class="cke_btn_reset" id="' + btnResetSizeId + '" role="button"><span class="cke_label">' + editor.lang.image.resetSize + '</span></a>' +
                                                        '</div>'
                                                }
                                            ]
                                        },
                                        {
                                            type: 'vbox',
                                            padding: 1,
                                            children: [
                                                {
                                                    type: 'text',
                                                    id: 'txtBorder',
                                                    requiredContent: 'img{border-width}',
                                                    width: '60px',
                                                    label: editor.lang.image.border,
                                                    'default': '',
                                                    onKeyUp: function () {
                                                        updatePreview(this.getDialog());
                                                    },
                                                    onChange: function () {
                                                        commitInternally.call(this, 'advanced:txtdlgGenStyle');
                                                    },
                                                    validate: CKEDITOR.dialog.validate.integer(editor.lang.image.validateBorder),
                                                    setup: function (type, element) {
                                                        if (type == IMAGE) {
                                                            var value,
                                                                borderStyle = element.getStyle('border-width');
                                                            borderStyle = borderStyle && borderStyle.match(/^(\d+px)(?: \1 \1 \1)?$/);
                                                            value = borderStyle && parseInt(borderStyle[ 1 ], 10);
                                                            isNaN(parseInt(value, 10)) && ( value = element.getAttribute('border') );
                                                            this.setValue(value);
                                                        }
                                                    },
                                                    commit: function (type, element, internalCommit) {
                                                        var value = parseInt(this.getValue(), 10);
                                                        if (type == IMAGE || type == PREVIEW) {
                                                            if (!isNaN(value)) {
                                                                element.setStyle('border-width', CKEDITOR.tools.cssLength(value));
                                                                element.setStyle('border-style', 'solid');
                                                            } else if (!value && this.isChanged())
                                                                element.removeStyle('border');

                                                            if (!internalCommit && type == IMAGE)
                                                                element.removeAttribute('border');
                                                        } else if (type == CLEANUP) {
                                                            element.removeAttribute('border');
                                                            element.removeStyle('border-width');
                                                            element.removeStyle('border-style');
                                                            element.removeStyle('border-color');
                                                        }
                                                    }
                                                },
                                                {
                                                    type: 'hbox',
                                                    style: 'width:100px;margin:0;',
                                                    children: [
                                                        {
                                                            type: 'text',
                                                            id: 'txtHSpace',
                                                            requiredContent: 'img{margin-left,margin-right}',
                                                            width: '60px',
                                                            label: editor.lang.image.hSpace,
                                                            'default': '',
                                                            onKeyUp: function () {
                                                                updatePreview(this.getDialog());
                                                            },
                                                            onChange: function () {
                                                                commitInternally.call(this, 'advanced:txtdlgGenStyle');
                                                            },
                                                            validate: CKEDITOR.dialog.validate.integer(editor.lang.image.validateHSpace),
                                                            setup: function (type, element) {
                                                                if (type == IMAGE) {
                                                                    var value, marginLeftPx, marginRightPx,
                                                                        marginLeftStyle = element.getStyle('margin-left'),
                                                                        marginRightStyle = element.getStyle('margin-right');

                                                                    marginLeftStyle = marginLeftStyle && marginLeftStyle.match(pxLengthRegex);
                                                                    marginRightStyle = marginRightStyle && marginRightStyle.match(pxLengthRegex);
                                                                    marginLeftPx = parseInt(marginLeftStyle, 10);
                                                                    marginRightPx = parseInt(marginRightStyle, 10);

                                                                    value = ( marginLeftPx == marginRightPx ) && marginLeftPx;
                                                                    isNaN(parseInt(value, 10)) && ( value = element.getAttribute('hspace') );

                                                                    this.setValue(value);
                                                                }
                                                            },
                                                            commit: function (type, element, internalCommit) {
                                                                var value = parseInt(this.getValue(), 10);
                                                                if (type == IMAGE || type == PREVIEW) {
                                                                    if (!isNaN(value)) {
                                                                        element.setStyle('margin-left', CKEDITOR.tools.cssLength(value));
                                                                        element.setStyle('margin-right', CKEDITOR.tools.cssLength(value));
                                                                    } else if (!value && this.isChanged()) {
                                                                        element.removeStyle('margin-left');
                                                                        element.removeStyle('margin-right');
                                                                    }

                                                                    if (!internalCommit && type == IMAGE)
                                                                        element.removeAttribute('hspace');
                                                                } else if (type == CLEANUP) {
                                                                    element.removeAttribute('hspace');
                                                                    element.removeStyle('margin-left');
                                                                    element.removeStyle('margin-right');
                                                                }
                                                            }
                                                        },
                                                        {
                                                            type: 'text',
                                                            id: 'txtVSpace',
                                                            requiredContent: 'img{margin-top,margin-bottom}',
                                                            width: '60px',
                                                            label: editor.lang.image.vSpace,
                                                            'default': '',
                                                            onKeyUp: function () {
                                                                updatePreview(this.getDialog());
                                                            },
                                                            onChange: function () {
                                                                commitInternally.call(this, 'advanced:txtdlgGenStyle');
                                                            },
                                                            validate: CKEDITOR.dialog.validate.integer(editor.lang.image.validateVSpace),
                                                            setup: function (type, element) {
                                                                if (type == IMAGE) {
                                                                    var value, marginTopPx, marginBottomPx,
                                                                        marginTopStyle = element.getStyle('margin-top'),
                                                                        marginBottomStyle = element.getStyle('margin-bottom');

                                                                    marginTopStyle = marginTopStyle && marginTopStyle.match(pxLengthRegex);
                                                                    marginBottomStyle = marginBottomStyle && marginBottomStyle.match(pxLengthRegex);
                                                                    marginTopPx = parseInt(marginTopStyle, 10);
                                                                    marginBottomPx = parseInt(marginBottomStyle, 10);

                                                                    value = ( marginTopPx == marginBottomPx ) && marginTopPx;
                                                                    isNaN(parseInt(value, 10)) && ( value = element.getAttribute('vspace') );
                                                                    this.setValue(value);
                                                                }
                                                            },
                                                            commit: function (type, element, internalCommit) {
                                                                var value = parseInt(this.getValue(), 10);
                                                                if (type == IMAGE || type == PREVIEW) {
                                                                    if (!isNaN(value)) {
                                                                        element.setStyle('margin-top', CKEDITOR.tools.cssLength(value));
                                                                        element.setStyle('margin-bottom', CKEDITOR.tools.cssLength(value));
                                                                    } else if (!value && this.isChanged()) {
                                                                        element.removeStyle('margin-top');
                                                                        element.removeStyle('margin-bottom');
                                                                    }

                                                                    if (!internalCommit && type == IMAGE)
                                                                        element.removeAttribute('vspace');
                                                                } else if (type == CLEANUP) {
                                                                    element.removeAttribute('vspace');
                                                                    element.removeStyle('margin-top');
                                                                    element.removeStyle('margin-bottom');
                                                                }
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    id: 'cmbAlign',
                                                    requiredContent: 'img{float}',
                                                    type: 'select',
                                                    widths: [ '35%', '65%' ],
                                                    style: 'width:90px',
                                                    label: editor.lang.common.align,
                                                    'default': '',
                                                    items: [
                                                        [ editor.lang.common.notSet, '' ],
                                                        [ editor.lang.common.alignLeft, 'left' ],
                                                        [ editor.lang.common.alignRight, 'right' ]
                                                        // Backward compatible with v2 on setup when specified as attribute value,
                                                        // while these values are no more available as select options.
                                                        //	[ editor.lang.image.alignAbsBottom , 'absBottom'],
                                                        //	[ editor.lang.image.alignAbsMiddle , 'absMiddle'],
                                                        //  [ editor.lang.image.alignBaseline , 'baseline'],
                                                        //  [ editor.lang.image.alignTextTop , 'text-top'],
                                                        //  [ editor.lang.image.alignBottom , 'bottom'],
                                                        //  [ editor.lang.image.alignMiddle , 'middle'],
                                                        //  [ editor.lang.image.alignTop , 'top']
                                                    ],
                                                    onChange: function () {
                                                        updatePreview(this.getDialog());
                                                        commitInternally.call(this, 'advanced:txtdlgGenStyle');
                                                    },
                                                    setup: function (type, element) {
                                                        if (type == IMAGE) {
                                                            var value = element.getStyle('float');
                                                            switch (value) {
                                                                // Ignore those unrelated values.
                                                                case 'inherit':
                                                                case 'none':
                                                                    value = '';
                                                            }

                                                            !value && ( value = ( element.getAttribute('align') || '' ).toLowerCase() );
                                                            this.setValue(value);
                                                        }
                                                    },
                                                    commit: function (type, element, internalCommit) {
                                                        var value = this.getValue();
                                                        if (type == IMAGE || type == PREVIEW) {
                                                            if (value)
                                                                element.setStyle('float', value);
                                                            else
                                                                element.removeStyle('float');

                                                            if (!internalCommit && type == IMAGE) {
                                                                value = ( element.getAttribute('align') || '' ).toLowerCase();
                                                                switch (value) {
                                                                    // we should remove it only if it matches "left" or "right",
                                                                    // otherwise leave it intact.
                                                                    case 'left':
                                                                    case 'right':
                                                                        element.removeAttribute('align');
                                                                }
                                                            }
                                                        } else if (type == CLEANUP)
                                                            element.removeStyle('float');

                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                }

                            ]
                        }
                    ]
                },
                {
                    id: 'Link',
                    requiredContent: 'a[href]',
                    label: editor.lang.image.linkTab,
                    padding: 0,
                    elements: [
                        {
                            id: 'txtUrl',
                            type: 'text',
                            label: editor.lang.common.url,
                            style: 'width: 100%',
                            'default': '',
                            setup: function (type, element) {
                                if (type == LINK) {
                                    var href = element.data('cke-saved-href');
                                    if (!href)
                                        href = element.getAttribute('href');
                                    this.setValue(href);
                                }
                            },
                            commit: function (type, element) {
                                if (type == LINK) {
                                    if (this.getValue() || this.isChanged()) {
                                        var url = decodeURI(this.getValue());
                                        element.data('cke-saved-href', url);
                                        element.setAttribute('href', url);

                                        if (this.getValue() || !editor.config.image_removeLinkByEmptyURL)
                                            this.getDialog().addLink = true;
                                    }
                                }
                            }
                        },
                        {
                            type: 'button',
                            id: 'browse',
                            filebrowser: {
                                action: 'Browse',
                                target: 'Link:txtUrl',
                                url: editor.config.filebrowserImageBrowseLinkUrl
                            },
                            style: 'float:right',
                            hidden: true,
                            label: editor.lang.common.browseServer
                        },
                        {
                            id: 'cmbTarget',
                            type: 'select',
                            requiredContent: 'a[target]',
                            label: editor.lang.common.target,
                            'default': '',
                            items: [
                                [ editor.lang.common.notSet, '' ],
                                [ editor.lang.common.targetNew, '_blank' ],
                                [ editor.lang.common.targetTop, '_top' ],
                                [ editor.lang.common.targetSelf, '_self' ],
                                [ editor.lang.common.targetParent, '_parent' ]
                            ],
                            setup: function (type, element) {
                                if (type == LINK)
                                    this.setValue(element.getAttribute('target') || '');
                            },
                            commit: function (type, element) {
                                if (type == LINK) {
                                    if (this.getValue() || this.isChanged())
                                        element.setAttribute('target', this.getValue());
                                }
                            }
                        }
                    ]
                },
                {
                    id: 'advanced',
                    label: editor.lang.common.advancedTab,
                    elements: [
                        {
                            type: 'hbox',
                            widths: [ '50%', '25%', '25%' ],
                            children: [
                                {
                                    type: 'text',
                                    id: 'linkId',
                                    requiredContent: 'img[id]',
                                    label: editor.lang.common.id,
                                    setup: function (type, element) {
                                        if (type == IMAGE)
                                            this.setValue(element.getAttribute('id'));
                                    },
                                    commit: function (type, element) {
                                        if (type == IMAGE) {
                                            if (this.getValue() || this.isChanged())
                                                element.setAttribute('id', this.getValue());
                                        }
                                    }
                                },
                                {
                                    id: 'cmbLangDir',
                                    type: 'select',
                                    requiredContent: 'img[dir]',
                                    style: 'width : 100px;',
                                    label: editor.lang.common.langDir,
                                    'default': '',
                                    items: [
                                        [ editor.lang.common.notSet, '' ],
                                        [ editor.lang.common.langDirLtr, 'ltr' ],
                                        [ editor.lang.common.langDirRtl, 'rtl' ]
                                    ],
                                    setup: function (type, element) {
                                        if (type == IMAGE)
                                            this.setValue(element.getAttribute('dir'));
                                    },
                                    commit: function (type, element) {
                                        if (type == IMAGE) {
                                            if (this.getValue() || this.isChanged())
                                                element.setAttribute('dir', this.getValue());
                                        }
                                    }
                                },
                                {
                                    type: 'text',
                                    id: 'txtLangCode',
                                    requiredContent: 'img[lang]',
                                    label: editor.lang.common.langCode,
                                    'default': '',
                                    setup: function (type, element) {
                                        if (type == IMAGE)
                                            this.setValue(element.getAttribute('lang'));
                                    },
                                    commit: function (type, element) {
                                        if (type == IMAGE) {
                                            if (this.getValue() || this.isChanged())
                                                element.setAttribute('lang', this.getValue());
                                        }
                                    }
                                }
                            ]
                        },
                        {
                            type: 'text',
                            id: 'txtGenLongDescr',
                            requiredContent: 'img[longdesc]',
                            label: editor.lang.common.longDescr,
                            setup: function (type, element) {
                                if (type == IMAGE)
                                    this.setValue(element.getAttribute('longDesc'));
                            },
                            commit: function (type, element) {
                                if (type == IMAGE) {
                                    if (this.getValue() || this.isChanged())
                                        element.setAttribute('longDesc', this.getValue());
                                }
                            }
                        },
                        {
                            type: 'hbox',
                            widths: [ '50%', '50%' ],
                            children: [
                                {
                                    type: 'text',
                                    id: 'txtGenClass',
                                    requiredContent: 'img(cke-xyz)', // Random text like 'xyz' will check if all are allowed.
                                    label: editor.lang.common.cssClass,
                                    'default': '',
                                    setup: function (type, element) {
                                        if (type == IMAGE)
                                            this.setValue(element.getAttribute('class'));
                                    },
                                    commit: function (type, element) {
                                        if (type == IMAGE) {
                                            if (this.getValue() || this.isChanged())
                                                element.setAttribute('class', this.getValue());
                                        }
                                    }
                                },
                                {
                                    type: 'text',
                                    id: 'txtGenTitle',
                                    requiredContent: 'img[title]',
                                    label: editor.lang.common.advisoryTitle,
                                    'default': '',
                                    onChange: function () {
                                        updatePreview(this.getDialog());
                                    },
                                    setup: function (type, element) {
                                        if (type == IMAGE)
                                            this.setValue(element.getAttribute('title'));
                                    },
                                    commit: function (type, element) {
                                        if (type == IMAGE) {
                                            if (this.getValue() || this.isChanged())
                                                element.setAttribute('title', this.getValue());
                                        } else if (type == PREVIEW) {
                                            element.setAttribute('title', this.getValue());
                                        } else if (type == CLEANUP) {
                                            element.removeAttribute('title');
                                        }
                                    }
                                }
                            ]
                        },
                        {
                            type: 'text',
                            id: 'txtdlgGenStyle',
                            requiredContent: 'img{cke-xyz}', // Random text like 'xyz' will check if all are allowed.
                            label: editor.lang.common.cssStyle,
                            validate: CKEDITOR.dialog.validate.inlineStyle(editor.lang.common.invalidInlineStyle),
                            'default': '',
                            setup: function (type, element) {
                                if (type == IMAGE) {
                                    var genStyle = element.getAttribute('style');
                                    if (!genStyle && element.$.style.cssText)
                                        genStyle = element.$.style.cssText;
                                    this.setValue(genStyle);

                                    var height = element.$.style.height,
                                        width = element.$.style.width,
                                        aMatchH = ( height ? height : '' ).match(regexGetSize),
                                        aMatchW = ( width ? width : '' ).match(regexGetSize);

                                    this.attributesInStyle = {
                                        height: !!aMatchH,
                                        width: !!aMatchW
                                    };
                                }
                            },
                            onChange: function () {
                                commitInternally.call(this, [ 'info:cmbFloat', 'info:cmbAlign',
                                    'info:txtVSpace', 'info:txtHSpace',
                                    'info:txtBorder',
                                    'info:txtWidth', 'info:txtHeight' ]);
                                updatePreview(this);
                            },
                            commit: function (type, element) {
                                if (type == IMAGE && ( this.getValue() || this.isChanged() )) {
                                    element.setAttribute('style', this.getValue());
                                }
                            }
                        }
                    ]
                }
            ]
        };
    };

    CKEDITOR.dialog.add('mayoimage', function (editor) {
        return imageDialog(editor, 'mayoimage');
    });

    CKEDITOR.dialog.add('imagebutton2', function (editor) {
        return imageDialog(editor, 'imagebutton2');
    });
})();
