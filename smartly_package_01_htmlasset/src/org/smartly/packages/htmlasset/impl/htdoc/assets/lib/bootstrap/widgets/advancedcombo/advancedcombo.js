(function () {

    lyb.require('/assets/lib/bootstrap/widgets/advancedcombo/advancedcombo.mini.css');

    var EVENT_SELECT = 'select'
        , EVENT_EDIT = 'edit'
        , EVENT_REMOVE = 'remove'
        , EVENT_ADD = 'add'
        , EVENT_SEARCH = 'search'

        , TEMPLATE = '<option value="{_id}">{name}</option>'

        , sel_sef = '#<%= cid %>'

        , sel_title = '#title-<%= cid %>'
        , sel_found = '#found-<%= cid %>'
        , sel_total = '#total-<%= cid %>'
        , sel_add = '#add-<%= cid %>'
        , sel_edit = '#edit-<%= cid %>'
        , sel_remove = '#remove-<%= cid %>'
        , sel_search_box = '#search-box-<%= cid %>'
        , sel_search = '#search-<%= cid %>'
        , sel_search_text = '#search-text-<%= cid %>'
        , sel_confirm_remove = '#confirm-remove-<%= cid %>'
        , sel_confirm_undo = '#confirm-undo-<%= cid %>'

        , sel_buttons = '#buttons-<%= cid %>'
        , sel_confirm = '#confirm-<%= cid %>'

        , sel_combo = '#combo-<%= cid %>'
        , sel_icon = '#icon-<%= cid %>'
        , sel_thumb = '#thumb-<%= cid %>'
        , sel_thumb_image = '#thumb_image-<%= cid %>'
        , sel_thumb_description = '#thumb_description-<%= cid %>'

        ;

    function AdvancedCombo(options) {
        var self = this
            ;

        ly.base(this, {
            template: '/assets/lib/bootstrap/widgets/advancedcombo/advancedcombo.vhtml',
            model: false,
            view: true
        });

        options = options || {}; // avoid error on null options

        self.set(options);

        self['_selected'] = "";

        self.bindTo(_refreshOptions)();

        // add listeners
        self.on('init', _init);
    }

    ly.inherits(AdvancedCombo, ly.Gui);

    AdvancedCombo.prototype.appendTo = function (parent, callback) {
        var self = this;
        ly.base(self, 'appendTo', parent, function () {
            self.bindTo(_initComponents)(callback);
        });
    };

    AdvancedCombo.prototype.set = function (options) {
        var self = this;
        if (!!options) {
            self['_title'] = options['title'] || '';
            self['_add'] = null != options['add'] ? !!options['add'] : true;
            self['_add_tooltip'] = options['_add_tooltip'] || '';
            self['_remove'] = null != options['remove'] ? !!options['remove'] : true;
            self['_remove_tooltip'] = options['_remove_tooltip'] || '';
            self['_edit'] = null != options['edit'] ? !!options['editd'] : true;
            self['_edit_tooltip'] = options['_edit_tooltip'] || '';
            self['_search'] = null != options['search'] ? !!options['search'] : true;
            self['_search_tooltip'] = options['_search_tooltip'] || '';
            self['_search_placeholder'] = options['_search_placeholder'] || '';

            self['_icon'] = options['icon'] || 'icon-asterisk';
            self['_size'] = options['size'] || 'input-medium';

            self['_skip'] = null != options['skip'] ? options['skip'] : null != self['_skip'] ? self['_skip'] : 0;
            self['_limit'] = null != options['limit'] ? options['limit'] : null != self['_limit'] ? self['_limit'] : 5;
            self['_page_nr'] = null != options['page_nr'] ? options['page_nr'] : null != self['_page_nr'] ? self['_page_nr'] : 1;
            self['_page_count'] = null != options['page_count'] ? options['page_count'] : null != self['_page_count'] ? self['_page_count'] : 1;
            self['_total'] = null != options['total'] ? options['total'] : null != self['_total'] ? self['_total'] : 0;

            self['_items'] = null != options['items'] ? options['items'] : self['_items'] || [];

            //-- item attributes --//
            if (null == self['_item_id']) {
                self['_item_id'] = null != options['_item_id'] ? options['_item_id'] : '_id';
            }
            if (null == self['_item_name']) {
                self['_item_name'] = null != options['item_name'] ? options['item_name'] : 'name';
            }
            if (null == self['_item_description']) {
                self['_item_description'] = null != options['item_description'] ? options['item_description'] : 'description';
            }
            if (null == self['_item_image']) {
                self['_item_image'] = null != options['item_image'] ? options['item_image'] : 'image';
            }

            if (_.isArray(options['items'])) {
                self.bindTo(_loadItems)(options['items']);
            }
        }
    };

    AdvancedCombo.prototype.select = function (item, emitEvent) {
        this.bindTo(_select)(item, emitEvent);
    };

    AdvancedCombo.prototype.selected = function () {
        return this['_selected'];
    };

    AdvancedCombo.prototype.items = function (items) {
        if (!!items) {
            this['_items'] = items;
            this.bindTo(_init)();
        }
        return this['_items'];
    };

    AdvancedCombo.prototype.search = function () {
        return '';
    };

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _init() {
        var self = this
            , $combo = $(self.template(sel_combo))
            , $icon = $(self.template(sel_icon))
            ;

        self.bindTo(_loadItems)(self['_items']);


        // icon
        $icon.addClass(self['_icon']);

        // size
        var size = self['_size'];
        if (size.indexOf('px') > -1 || size.indexOf('%') > -1) {
            $combo.attr('style', 'width:' + size + ';')
        } else {
            $combo.addClass(size);
        }
    }

    function _initComponents(callback) {
        var self = this
            ;

        self.bindTo(_initParentOptions)();

        self.bindTo(_refreshOptions)();

        self.bindTo(_initHandlers)();

        self.bindTo(_toggleConfirm)(false);

        ly.call(callback);
    }

    function _initParentOptions() {
        var self = this;

        //-- title --//
        var title = self.parent.attr('data-title');
        if (!!title && !self['_title']) {
            self['_title'] = title;
        }

        //-- add --//
        var add = self.parent.attr('data-add');
        self['_add'] = null == add || ly.toBoolean(add)
        if (self['_add']) {
            var add_tooltip = self.parent.attr('data-add-tooltip');
            if (!!add_tooltip && !self['_add_tooltip']) {
                self['_add_tooltip'] = add_tooltip;
            }
        }

        //-- remove --//
        var remove = self.parent.attr('data-remove');
        self['_remove'] = null == remove || ly.toBoolean(remove);
        if (self['_remove']) {
            var remove_tooltip = self.parent.attr('data-remove-tooltip');
            if (!!remove_tooltip && !self['_remove_tooltip']) {
                self['_remove_tooltip'] = remove_tooltip;
            }
        }

        //-- edit --//
        var edit = self.parent.attr('data-edit');
        self['_edit'] = null == edit || ly.toBoolean(edit);
        if (self['_edit']) {
            var edit_tooltip = self.parent.attr('data-edit-tooltip');
            if (!!edit_tooltip && !self['_edit_tooltip']) {
                self['_edit_tooltip'] = edit_tooltip;
            }
        }

        //-- search --//
        var search = self.parent.attr('data-search');
        self['_search'] = null == search || ly.toBoolean(search);
        if (self['_search']) {
            var search_tooltip = self.parent.attr('data-search-tooltip');
            if (!!search_tooltip && !self['_search_tooltip']) {
                self['_search_tooltip'] = search_tooltip;
            }
            var search_placeholder = self.parent.attr('data-search-placeholder');
            if (!!search_placeholder && !self['_search_placeholder']) {
                self['_search_placeholder'] = search_placeholder;
            }
        }
    }

    function _refreshOptions() {
        var self = this;

        //-- title --//
        var $title = $(self.template(sel_title));
        $title.html(self['_title']);

        //-- add --//
        var $add = $(self.template(sel_add));
        if (!!self['_add']) {
            $add.show();
            $add.attr('title', self['_add_tooltip']);
        } else {
            $add.hide();
        }

        //-- remove --//
        var $remove = $(self.template(sel_remove));
        if (!!self['_remove']) {
            $remove.show();
            $remove.attr('title', self['_remove_tooltip']);
        } else {
            $remove.hide();
        }

        //-- edit --//
        var $edit = $(self.template(sel_edit));
        if (!!self['_edit']) {
            $edit.show();
            $edit.attr('title', self['_edit_tooltip']);
        } else {
            $edit.hide();
        }

        //-- search --//
        var $search_box = $(self.template(sel_search_box));
        if (!!self['_search']) {
            $search_box.show();
            if (!!self['_search_tooltip']) {
                $(self.template(sel_search)).attr('title', self['_search_tooltip']);
            }
            if (!!self['_search_placeholder']) {
                $(self.template(sel_search_text)).attr('placeholder', self['_search_placeholder']);
            }
        } else {
            $search_box.hide();
        }
    }


    function _loadItems(items) {
        var self = this;
        var $combo = $(self.template(sel_combo))

        $combo.html('');
        if (_.isArray(items) && items.length>0) {
            var field_id = self['_item_id'];
            var field_label = self['_item_name'];
            // creates items
            var count = 0;
            _.forEach(items, function (item) {
                if (!ly.isNull(item)) {
                    count++;
                    var data = {_id: item[field_id], name: item[field_label]};
                    var $item = $(ly.template(TEMPLATE, data));
                    $item.appendTo($combo);
                }
            });

            // update total
            if (count < 2) {
                // hide Found:
                $(self.template(sel_found)).hide();
            } else {
                $(self.template(sel_total)).html(count);
            }

            // select first
            self['_selected'] = items[0];
            // async call for gui
            self.select(self['_selected']);
        }
    }

    function _changeItem(component) {
        try {
            var id = ly.el.value($(component));
            // selected
            this.bindTo(_select)(id, true);
        } catch (err) {
            ly.console.error(err);
        }
        return false;
    }

    function _itemById(id) {
        var result
            , field_id = this['_item_id']
            ;
        _.forEach(this['_items'], function (item) {
            if (item[field_id] === id) {
                result = item;
                return false;
            }
        });
        return result || this['_items'][0];
    }

    function _select(item_or_string, emitevent) {
        var self = this;
        _.delay(function () {
            var item = _.isString(item_or_string) ? self.bindTo(_itemById)(item_or_string) : item_or_string
                ;
            self['_selected'] = item;

            // selected: show selection thumb
            self.bindTo(_showThumb)();

            if (!!emitevent) {
                self.trigger(EVENT_SELECT, item);
            }
        }, 100);
    }

    function _showThumb() {
        var self = this
            , item = self['_selected'];
        if (!item) {
            return;
        }
        var field_id = self['_item_id']
            , field_image = self['_item_image']
            , field_description = self['_item_description']
            , image = item[field_image]||''
            , description = item[field_description]
            , $thumb = $(self.template(sel_thumb))
            , $thumb_img = $(self.template(sel_thumb_image))
            , $thumb_des = $(self.template(sel_thumb_description))
            ;

        ly.el.value(self.template(sel_combo), item[field_id]);

        if (!!image) {
            // change image, name and description
            $thumb_img.attr('src', image);
            $thumb_des.html(description);
            // show thumb
            $thumb.fadeIn();
        } else {
            // hide thumb
            $thumb.hide();
        }
    }

    function _initHandlers() {
        var self = this;

        //-- search --//
        var $search = $(self.template(sel_search));
        var $search_text = $(self.template(sel_search_text));
        $search.tooltip();
        ly.el.click($search, function () {
            self.bindTo(_clickSearch)($search_text);
        });

        //-- add --//
        var $add = $(self.template(sel_add));
        $add.tooltip();
        ly.el.click($add, function () {
            self.bindTo(_clickAdd)();
        });
        //-- remove --//
        var $remove = $(self.template(sel_remove));
        $remove.tooltip();
        ly.el.click($remove, function () {
            self.bindTo(_clickRemove)();
        });
        //-- edit --//
        var $edit = $(self.template(sel_edit));
        $edit.tooltip();
        ly.el.click($edit, function () {
            self.bindTo(_clickEdit)();
        });


        // combo
        var $combo = $(self.template(sel_combo));
        $combo.unbind();
        $combo.on('change', function (e) {
            e.stopImmediatePropagation();
            return self.bindTo(_changeItem)(this);
        });

        //-- confirm remove --//
        var $confirm_remove = $(self.template(sel_confirm_remove));
        ly.el.click($confirm_remove, function () {
            self.bindTo(_confirmRemove);
        });
        //-- confirm undo --//
        var $confirm_undo = $(self.template(sel_confirm_undo));
        ly.el.click($confirm_undo, function () {
            self.bindTo(_toggleConfirm)(false);
        });
    }

    function _toggleConfirm(show) {
        var self = this;
        var $confirm = $(self.template(sel_confirm));
        var $buttons = $(self.template(sel_buttons));

        if (show) {
            $confirm.show();
            $buttons.hide();
        } else {
            $confirm.hide();
            $buttons.show();
        }
    }

    function _clickSearch($search_text) {
        var self = this;
        var txt = ly.el.value($search_text);

        //-- reset paging--//
        self['_skip'] = 0;
        self['_page_nr'] = 1;
        self['_page_count'] = 1;

        //-- trigger event --//
        self.trigger(EVENT_SEARCH, txt);
    }

    function _clickEdit() {
        var self = this;
        var item = self.selected();
        if (!!item) {
            self.trigger(EVENT_EDIT, item);
        }
    }

    function _clickAdd() {
        var self = this;
        self.trigger(EVENT_ADD, '');
    }

    function _clickRemove() {
        var self = this;
        var item = self.selected();
        if (!!item) {
            // show remove question
            self.bindTo(_toggleConfirm)(true);
        }
    }

    function _confirmRemove() {
        var self = this;
        var item = self.selected();
        if (!!item) {
            // show remove question
            self.trigger(EVENT_REMOVE, item);
        }
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('ly.gui.widgets.AdvancedCombo');
    ly.gui.widgets.AdvancedCombo = AdvancedCombo;

})();
